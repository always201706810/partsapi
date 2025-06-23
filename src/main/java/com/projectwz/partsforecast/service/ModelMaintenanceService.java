package com.projectwz.partsforecast.service;

import com.projectwz.partsforecast.dto.AgentDatasetDTO;
import com.projectwz.partsforecast.dto.ModelManageInfoDTO;
import com.projectwz.partsforecast.dto.PartsStatsDTO;
import com.projectwz.partsforecast.dto.CreateModelRequestDTO;
import com.projectwz.partsforecast.entity.AgentInfo;
import com.projectwz.partsforecast.entity.ModelManage;
import com.projectwz.partsforecast.entity.ModelStatus;
import com.projectwz.partsforecast.entity.TrainInfo;
// 已更改: 导入 Mappers
import com.projectwz.partsforecast.mapper.AgentInfoMapper;
import com.projectwz.partsforecast.mapper.ModelManageMapper;
import com.projectwz.partsforecast.mapper.TrainInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelMaintenanceService {

    // 已更改: 注入 Mappers
    @Autowired private ModelManageMapper modelManageMapper;
    @Autowired private AgentInfoMapper agentInfoMapper;
    @Autowired private TrainInfoMapper trainInfoMapper;

    @Autowired private RestTemplate restTemplate;

    @Value("${flask.service.url}")
    private String flaskServiceUrl;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<AgentDatasetDTO> getAllAgentDatasets() {
        // 已更改: 调用 Mapper 方法
        List<AgentInfo> agents = agentInfoMapper.findAll();
        if (agents.isEmpty()) {
            System.out.println("Service: No agents found in agent_info table.");
        }
        return agents.stream()
                .map(agent -> new AgentDatasetDTO(agent.getId(), agent.getAgentName() + "数据集"))
                .collect(Collectors.toList());
    }

    public PartsStatsDTO getPartsStatsByAgentId(Integer agentId) {
        // 已更改: 调用 Mapper 方法
        List<TrainInfo> agentTrainInfos = trainInfoMapper.findByAgentId(agentId);
        long totalPartsInTrainInfoForAgent = agentTrainInfos.size();
        long headParts = agentTrainInfos.stream()
                .filter(ti -> ti.getPartsLen() != null)
                .filter(ti -> { try { return Integer.parseInt(ti.getPartsLen()) >= 200; } catch (NumberFormatException e) { return false; }})
                .count();
        long tailParts = totalPartsInTrainInfoForAgent - headParts;
        return new PartsStatsDTO(totalPartsInTrainInfoForAgent, headParts, tailParts);
    }

    public List<ModelManageInfoDTO> searchModels(String nameQuery, LocalDateTime creationStart, LocalDateTime creationEnd, LocalDateTime trainingStart, LocalDateTime trainingEnd) {
        // 已更改: 使用 Map 传递动态查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("nameQuery", nameQuery);
        params.put("creationStart", creationStart);
        params.put("creationEnd", creationEnd);
        params.put("trainingStart", trainingStart);
        params.put("trainingEnd", trainingEnd);

        return modelManageMapper.searchModels(params).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ModelManageInfoDTO> createModelsForAgent(CreateModelRequestDTO requestDTO) {
        AgentInfo agent = agentInfoMapper.findById(requestDTO.getAgentId())
                .orElseThrow(() -> new RuntimeException("代理商/数据集未找到，ID: " + requestDTO.getAgentId()));

        List<ModelManage> createdModelsInDb = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        String[] modelTypes = {"DPIBT", "EATC-GAN"};
        for (String typeSuffix : modelTypes) {
            ModelManage model = new ModelManage();
            model.setModelName(requestDTO.getBaseName() + " - " + typeSuffix);
            model.setModelType(typeSuffix.equals("DPIBT") ? 1 : 2);
            model.setModelDescribe(requestDTO.getDescription());
            model.setCreateTime(now);
            model.setStatus(ModelStatus.PENDING);
            model.setAgentInfo(agent);

            // 已更改: 调用 insert 方法
            modelManageMapper.insert(model); // ID会回填到model对象中
            createdModelsInDb.add(model);

            triggerFlaskTraining(model.getId(), model.getAgentInfo().getId(), typeSuffix);
        }

        return createdModelsInDb.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Async
    public void triggerFlaskTraining(Integer modelId, Integer agentId, String modelTypeSuffix) {
        modelManageMapper.findById(modelId).ifPresent(model -> {
            model.setStatus(ModelStatus.TRAINING);
            // 已更改: 调用 update 方法
            modelManageMapper.update(model);
        });

        String trainUrl = flaskServiceUrl + "/train";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model_id", modelId);
        requestBody.put("agent_id", agentId);
        requestBody.put("model_type", modelTypeSuffix);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        System.out.println("准备调用Flask服务触发训练: " + trainUrl + " with body: " + requestBody);

        try {
            restTemplate.postForObject(trainUrl, request, String.class);
        } catch (Exception e) {
            System.err.println("调用Flask服务失败: " + e.getMessage());
            modelManageMapper.findById(modelId).ifPresent(model -> {
                model.setStatus(ModelStatus.FAILED);
                model.setModelDescribe(model.getModelDescribe() + " | 触发训练失败: " + e.getMessage());
                // 已更改: 调用 update 方法
                modelManageMapper.update(model);
            });
        }
    }

    public ModelManageInfoDTO getModelDetails(Integer modelId) {
        ModelManage model = modelManageMapper.findById(modelId)
                .orElseThrow(() -> new RuntimeException("模型未找到或已被删除, ID: " + modelId));
        return convertToDTO(model);
    }

    public ModelManageInfoDTO retrainModel(Integer modelId) {
        ModelManage model = modelManageMapper.findById(modelId)
                .orElseThrow(() -> new RuntimeException("模型未找到或已被删除, ID: " + modelId));

        if (model.getAgentInfo() == null) {
            throw new RuntimeException("模型 ID " + modelId + " 没有关联的代理商信息，无法重新训练。");
        }
        Integer agentId = model.getAgentInfo().getId();

        String modelTypeSuffix = model.getModelType() == 1 ? "DPIBT" : "EATC-GAN";
        model.setStatus(ModelStatus.PENDING);
        model.setTrainTime(LocalDateTime.now());

        // 已更改: 调用 update 方法
        modelManageMapper.update(model);

        triggerFlaskTraining(model.getId(), agentId, modelTypeSuffix);
        System.out.println(String.format("模型ID %d 已提交重新训练任务。", modelId));
        return convertToDTO(model);
    }

    public void deleteModel(Integer modelId) {
        ModelManage model = modelManageMapper.findById(modelId)
                .orElseThrow(() -> new RuntimeException("模型未找到, ID: " + modelId));
        model.setIsDeleted(1);
        // 已更改: 调用 update 方法
        modelManageMapper.update(model);
    }

    public void updateModelStatus(Integer modelId, ModelStatus newStatus, String errorMessage) {
        ModelManage model = modelManageMapper.findById(modelId)
                .orElseThrow(() -> new RuntimeException("尝试更新状态时，未找到模型, ID: " + modelId));

        model.setStatus(newStatus);
        if (newStatus == ModelStatus.FAILED && errorMessage != null) {
            model.setModelDescribe(model.getModelDescribe() + " | 训练失败原因: " + errorMessage);
        }

        // 已更改: 调用 update 方法
        modelManageMapper.update(model);
        System.out.println(String.format("模型ID %d 的状态已通过回调更新为: %s", modelId, newStatus.getDescription()));
    }

    private ModelManageInfoDTO convertToDTO(ModelManage model) {
        ModelManageInfoDTO dto = new ModelManageInfoDTO();
        BeanUtils.copyProperties(model, dto);
        dto.setName(model.getModelName());
        if (model.getCreateTime() != null) dto.setCreationTime(model.getCreateTime().format(formatter));
        if (model.getTrainTime() != null) dto.setLastTrainingTime(model.getTrainTime().format(formatter));
        dto.setType(model.getModelType() != null && model.getModelType() == 2 ? "数据增强预测" : "预测");
        if (model.getAgentInfo() != null) {
            dto.setModelDescribe("用于" + model.getAgentInfo().getAgentName());
            dto.setAgentId(model.getAgentInfo().getId());
        } else {
            dto.setModelDescribe("未关联代理商");
        }
        if (model.getStatus() != null) {
            dto.setStatus(model.getStatus().getDescription());
        } else {
            dto.setStatus(model.getIsDeleted() == 1 ? "已删除" : "未知");
        }
        dto.setPerformanceChartPath(model.getModelMetricsPhoto());
        return dto;
    }
}