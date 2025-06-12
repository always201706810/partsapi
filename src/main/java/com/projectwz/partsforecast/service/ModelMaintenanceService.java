package com.projectwz.partsforecast.service;

import com.projectwz.partsforecast.dto.AgentDatasetDTO;
import com.projectwz.partsforecast.dto.ModelManageInfoDTO;
import com.projectwz.partsforecast.dto.PartsStatsDTO;
import com.projectwz.partsforecast.dto.CreateModelRequestDTO; // 新增
import com.projectwz.partsforecast.entity.AgentInfo; // 需要 AgentInfo 实体
import com.projectwz.partsforecast.entity.ModelManage;
import com.projectwz.partsforecast.entity.ModelStatus;
import com.projectwz.partsforecast.entity.TrainInfo;
import com.projectwz.partsforecast.repository.AgentInfoRepository;
import com.projectwz.partsforecast.repository.ModelManageRepository;
import com.projectwz.partsforecast.repository.PartsInfoRepository;
import com.projectwz.partsforecast.repository.TrainInfoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification; // 用于搜索
import org.springframework.stereotype.Service;
import javax.persistence.criteria.Predicate; // 用于搜索
//import jakarta.persistence.criteria.Predicate; // 用于搜索
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value; // 用于从配置文件读取Flask URL
import org.springframework.scheduling.annotation.Async; // 导入异步注解
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelMaintenanceService {

    @Autowired private ModelManageRepository modelManageRepository;
    @Autowired private AgentInfoRepository agentInfoRepository;
    @Autowired private TrainInfoRepository trainInfoRepository;
    @Autowired private PartsInfoRepository partsInfoRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${flask.service.url}")
    private String flaskServiceUrl;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- 之前已有的方法 ---
    public List<AgentDatasetDTO> getAllAgentDatasets() {
        List<AgentInfo> agents = agentInfoRepository.findAll(); // 这会执行 "select ai1_0.id,ai1_0.agent_name from agent_info ai1_0"
        if (agents.isEmpty()) {
            System.out.println("Service: No agents found in agent_info table."); // 添加日志
        }
        return agents.stream()
                .map(agent -> new AgentDatasetDTO(agent.getId(), agent.getAgentName() + "数据集"))
                .collect(Collectors.toList());
    }

    public PartsStatsDTO getPartsStatsByAgentId(Integer agentId) {
        List<TrainInfo> agentTrainInfos = trainInfoRepository.findByPartsInfo_AgentInfo_Id(agentId);
        long totalPartsInTrainInfoForAgent = agentTrainInfos.size();
        long headParts = agentTrainInfos.stream()
                .filter(ti -> ti.getPartsLen() != null)
                .filter(ti -> { try { return Integer.parseInt(ti.getPartsLen()) >= 200; } catch (NumberFormatException e) { return false; }})
                .count();
        long tailParts = totalPartsInTrainInfoForAgent - headParts; // 更简单的计算尾部
        return new PartsStatsDTO(totalPartsInTrainInfoForAgent, headParts, tailParts);
    }

    // --- 新增和完善的方法 ---

    /**
     * 获取模型列表（支持筛选）
     */
    public List<ModelManageInfoDTO> searchModels(String nameQuery, LocalDateTime creationStart, LocalDateTime creationEnd, LocalDateTime trainingStart, LocalDateTime trainingEnd) {
        Specification<ModelManage> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), 0)); // 只查询未删除的

            if (nameQuery != null && !nameQuery.isEmpty()) {
                predicates.add(cb.like(root.get("modelName"), "%" + nameQuery + "%"));
            }
            if (creationStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), creationStart));
            }
            if (creationEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), creationEnd));
            }
            if (trainingStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("trainTime"), trainingStart));
            }
            if (trainingEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("trainTime"), trainingEnd));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return modelManageRepository.findAll(spec).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建模型 (一次创建两种类型 DPIBT 和 EATC-GAN)
     */
/*
    public List<ModelManageInfoDTO> createModelsForAgent(CreateModelRequestDTO requestDTO) {
        Optional<AgentInfo> agentOptional = agentInfoRepository.findById(requestDTO.getAgentId());
        if (agentOptional.isEmpty()) {
            throw new RuntimeException("代理商/数据集未找到，ID: " + requestDTO.getAgentId());
        }
        AgentInfo agent = agentOptional.get();
        String usage = "用于" + agent.getAgentName(); // 根据代理商设置用途

        List<ModelManage> createdModels = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 1. 创建 DPIBT (预测) 模型
        ModelManage dpibtModel = new ModelManage();
        dpibtModel.setModelName(requestDTO.getBaseName() + " - DPIBT");
        dpibtModel.setModelType(1); // 1: 预测
        dpibtModel.setModelDescribe(requestDTO.getDescription());
        dpibtModel.setCreateTime(now);
        // dpibtModel.setTrainTime(null); // 初始训练时间为空
        dpibtModel.setIsDeleted(0);
        // dpibtModel.setAgentInfo(agent); // 如果 ModelManage 实体有关联 AgentInfo 的字段
        // 使用字段需要前端在ModelManageInfoDTO中提供，或通过agentId设置usage
        // 这里我们通过 usage 字段来体现关联的代理商
        // 如果你的 ModelManage 实体有一个 agentId 字段，请在这里设置：
        // dpibtModel.setAgentId(requestDTO.getAgentId());

        // 为了在DTO中显示，可以先保存一个usage
        ModelManageInfoDTO tempDtoForUsage = new ModelManageInfoDTO();
        tempDtoForUsage.setAgentId(requestDTO.getAgentId());
        // dpibtModel.setUsage(usage); // usage 字段不在 ModelManage 实体中，而是DTO的概念
        // 如果实体中有 usage 字段，则取消注释

        createdModels.add(modelManageRepository.save(dpibtModel));

        // 2. 创建 EATC-GAN (数据增强预测) 模型
        ModelManage eatcGanModel = new ModelManage();
        eatcGanModel.setModelName(requestDTO.getBaseName() + " - EATC-GAN");
        eatcGanModel.setModelType(2); // 2: 数据增强
        eatcGanModel.setModelDescribe(requestDTO.getDescription());
        eatcGanModel.setCreateTime(now);
        eatcGanModel.setIsDeleted(0);
        // eatcGanModel.setAgentId(requestDTO.getAgentId()); // 如果实体中有 agentId 字段
        // eatcGanModel.setUsage(usage); // 如果实体中有 usage 字段

        createdModels.add(modelManageRepository.save(eatcGanModel));

        return createdModels.stream().map(model -> convertToDTO(model, agent.getAgentName())).collect(Collectors.toList());
    }
*/

    /**
     * 创建模型，并异步触发Flask训练任务
     */
    public List<ModelManageInfoDTO> createModelsForAgent(CreateModelRequestDTO requestDTO) {
        AgentInfo agent = agentInfoRepository.findById(requestDTO.getAgentId())
                .orElseThrow(() -> new RuntimeException("代理商/数据集未找到，ID: " + requestDTO.getAgentId()));

//        String usage = "用于" + agent.getAgentName();
//        List<ModelManage> createdModels = new ArrayList<>();
        List<ModelManage> createdModelsInDb = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 创建模型记录，初始状态为“待训练”
        // 你的逻辑是创建两种模型
        String[] modelTypes = {"DPIBT", "EATC-GAN"};
//        String[] modelTypes = {"DPIBT"};
        for (String typeSuffix : modelTypes) {
            ModelManage model = new ModelManage();
            model.setModelName(requestDTO.getBaseName() + " - " + typeSuffix);
            model.setModelType(typeSuffix.equals("DPIBT") ? 1 : 2); // 1: 预测, 2: 数据增强
            model.setModelDescribe(requestDTO.getDescription());
            model.setCreateTime(now);
            model.setIsDeleted(0);
            model.setStatus(ModelStatus.PENDING); // 使用枚举

            ModelManage savedModel = modelManageRepository.save(model);
            createdModelsInDb.add(savedModel);

            // 异步调用Flask服务来触发训练
            triggerFlaskTraining(savedModel.getId(), savedModel.getAgentInfo().getId(), typeSuffix);
        }

        return createdModelsInDb.stream()
                .map(model -> convertToDTO(model)) // 不再需要传递 agentName
                .collect(Collectors.toList());    }

    /**
     * 异步方法，用于调用Flask的训练接口
     * @param modelId 新创建的模型的ID
     * @param agentId 关联的数据集/代理商ID
     * @param modelTypeSuffix 模型类型后缀，用于告知Flask使用哪个算法
     */
    @Async // 标记为异步执行
    public void triggerFlaskTraining(Integer modelId, Integer agentId, String modelTypeSuffix) {
        // 更新模型状态为“训练中”
        modelManageRepository.findById(modelId).ifPresent(model -> {
            model.setStatus(ModelStatus.TRAINING); // 使用枚举
            modelManageRepository.save(model);
        });

        // 准备发送给Flask的数据
        String trainUrl = flaskServiceUrl + "/train"; // 假设Flask的训练端点是 /train
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 创建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model_id", modelId);
        requestBody.put("agent_id", agentId);
        requestBody.put("model_type", modelTypeSuffix); // 告知Flask是DPIBT还是EATC-GAN
        // 你可以根据 run.py 的需要传递更多参数，比如数据集的路径、超参数等

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        System.out.println("准备调用Flask服务触发训练: " + trainUrl + " with body: " + requestBody);

        try {
            // 发送POST请求
            // 这里我们不关心Flask的返回值，或者只关心它是否成功接收了任务
            String response = restTemplate.postForObject(trainUrl, request, String.class);
            System.out.println("Flask服务响应: " + response);
            // 这里可以根据Flask的响应做进一步处理，例如记录日志
            // 注意：此时Java方法已经返回，这个日志会在后台线程中打印
        } catch (Exception e) {
            System.err.println("调用Flask服务失败: " + e.getMessage());
            // 如果调用失败，需要处理模型状态，例如将其标记为“失败”
            modelManageRepository.findById(modelId).ifPresent(model -> {
                model.setStatus(ModelStatus.FAILED); // 使用枚举
                // 可以在描述中记录失败原因
                model.setModelDescribe(model.getModelDescribe() + " | 触发训练失败: " + e.getMessage());
                modelManageRepository.save(model);
            });
        }
    }

    /**
     * 获取模型详情
     */
    public ModelManageInfoDTO getModelDetails(Integer modelId) {
        ModelManage model = modelManageRepository.findById(modelId)
                .filter(m -> m.getIsDeleted() == 0)
                .orElseThrow(() -> new RuntimeException("模型未找到或已被删除, ID: " + modelId));
        // 尝试获取关联的代理商名称用于 usage 字段
        // 这需要 ModelManage 实体能关联到 AgentInfo，或者TrainInfo能找到对应Agent
        // 简化：如果ModelManage没有直接关联Agent，usage可能需要前端在创建时填充，或从模型名称解析
        // String agentNameForUsage = findAgentNameForModel(model);
        return convertToDTO(model); // 传递 null 表示无法轻易获取代理商名称
    }
    // 辅助方法，根据模型找到关联的代理商（如果需要，逻辑可能复杂）
    // private String findAgentNameForModel(ModelManage model) {
    //    // 示例：如果 TrainInfo 关联了 Model 和 Agent
    //    List<TrainInfo> trainInfos = trainInfoRepository.findByModelManageId(model.getId());
    //    if (!trainInfos.isEmpty() && trainInfos.get(0).getPartsInfo() != null && trainInfos.get(0).getPartsInfo().getAgentInfo() != null) {
    //        return trainInfos.get(0).getPartsInfo().getAgentInfo().getAgentName();
    //    }
    //    return "未知代理商"; // 或从模型名称解析
    // }


    /**
     * 重新训练模型
     * @param modelId 要重新训练的模型的ID
     * @return 返回更新了状态和训练时间的模型信息
     */
    public ModelManageInfoDTO retrainModel(Integer modelId) {
        // 1. 查找要重新训练的模型实体
        ModelManage model = modelManageRepository.findById(modelId)
                .filter(m -> m.getIsDeleted() == 0)
                .orElseThrow(() -> new RuntimeException("模型未找到或已被删除, ID: " + modelId));

        // 2. --- 核心简化：直接从模型实体中获取 agentId ---
        if (model.getAgentInfo() == null) {
            throw new RuntimeException("模型 ID " + modelId + " 没有关联的代理商信息，无法重新训练。");
        }
        Integer agentId = model.getAgentInfo().getId();


        // 3. 根据模型的 modelType (数字) 确定要传递给 Flask 的 model_type (字符串)
        String modelTypeSuffix = model.getModelType() == 1 ? "DPIBT" : "EATC-GAN";

        // 4. 更新模型状态为“待训练”或“训练中”，并更新最后训练时间
        model.setStatus(ModelStatus.PENDING); // 设置为待训练，Flask收到任务后会更新为训练中
        model.setTrainTime(LocalDateTime.now()); // 记录下重新训练的触发时间
        ModelManage updatedModel = modelManageRepository.save(model);

        // 5. 调用通用的异步方法来触发 Flask 训练
        triggerFlaskTraining(updatedModel.getId(), agentId, modelTypeSuffix);

        System.out.println(String.format("模型ID %d 已提交重新训练任务。", modelId));

        // 6. 返回更新后的模型信息给前端
        return convertToDTO(updatedModel); // convertToDTO 会处理 agentNameHint
    }

    /**
     * 删除模型 (软删除)
     */
    public void deleteModel(Integer modelId) {
        ModelManage model = modelManageRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("模型未找到, ID: " + modelId));
        model.setIsDeleted(1); // 标记为删除
        modelManageRepository.save(model);
    }

    // --- DTO转换辅助方法 ---
//    private ModelManageInfoDTO convertToDTO(ModelManage model) {
//        return convertToDTO(model, null); // 重载，方便内部调用
//    }
//
//    private ModelManageInfoDTO convertToDTO(ModelManage model, String agentNameHint) {
//        ModelManageInfoDTO dto = new ModelManageInfoDTO();
//        BeanUtils.copyProperties(model, dto); // 复制大部分属性
//
//        dto.setName(model.getModelName());
//        // 格式化日期
//        if (model.getCreateTime() != null) {
//            dto.setCreationTime(model.getCreateTime().format(formatter));
//        }
//        if (model.getTrainTime() != null) {
//            dto.setLastTrainingTime(model.getTrainTime().format(formatter));
//        }
//
//        // 转换模型类型和状态 (数据库存的是数字)
//        // 这些应该与前端的显示文本对应
//        dto.setType(model.getModelType() == 1 ? "预测" : (model.getModelType() == 2 ? "数据增强预测" : "未知类型"));
//        // dto.setStatus(model.getStatus()); // 假设实体有 status 字段，否则前端静态处理
//        // 示例：如何填充 usage, status (如果实体没有这些字段，DTO需要默认值或前端处理)
//        dto.setModelDescribe(model.getModelDescribe()); // <--- 将实体的 modelDescribe 赋给 DTO 的 modelDescribe
//        // 假设 status 也是需要转换或前端处理的
//        // dto.setStatus(model.getIsDeleted() == 0 ? "已完成" : "已删除"); // 简单示例
//        // 你的前端有 "训练中", "已完成" 等状态，这需要ModelManage实体有status字段
//        dto.setStatus("已完成"); // 默认值，需要实体支持
//
//        dto.setPerformanceChartPath(model.getModelMetricsPhoto()); // 映射 photo 字段
//
//        return dto;
//    }
//

    private ModelManageInfoDTO convertToDTO(ModelManage model) {
        ModelManageInfoDTO dto = new ModelManageInfoDTO();
        BeanUtils.copyProperties(model, dto);

        dto.setName(model.getModelName());
        if (model.getCreateTime() != null) dto.setCreationTime(model.getCreateTime().format(formatter));
        if (model.getTrainTime() != null) dto.setLastTrainingTime(model.getTrainTime().format(formatter));
        dto.setType(model.getModelType() != null && model.getModelType() == 2 ? "数据增强预测" : "预测");

        // 直接从关联的 AgentInfo 获取信息
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




    private String extractUsageFromModelName(String modelName) {
        if (modelName != null && modelName.contains("-")) {
            return "用于" + modelName.substring(0, modelName.indexOf("-"));
        }
        return "通用";
    }

    // ModelMaintenanceService.java
    public void updateModelStatus(Integer modelId, ModelStatus newStatus, String errorMessage) {
        ModelManage model = modelManageRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("尝试更新状态时，未找到模型, ID: " + modelId));

        model.setStatus(newStatus);
        if (newStatus == ModelStatus.FAILED && errorMessage != null) {
            model.setModelDescribe(model.getModelDescribe() + " | 训练失败原因: " + errorMessage);
        }

        modelManageRepository.save(model);
        System.out.println(String.format("模型ID %d 的状态已通过回调更新为: %s", modelId, newStatus.getDescription()));
    }





// ... (其他 Service 代码) ...

    // 核心转换方法
    private ModelManageInfoDTO convertToDTOInternal(ModelManage model, String agentNameHintOrUsageContext) {
        ModelManageInfoDTO dto = new ModelManageInfoDTO();

        // 使用 BeanUtils 复制大部分同名、同类型的属性
        // 注意：它不会复制 status (类型不同) 和日期 (需要格式化)
        // 它会复制 modelDescribe, modelMetricsMae, modelMetricsMse, modelMetricsFid 等
        BeanUtils.copyProperties(model, dto);

        // 手动处理需要特殊转换的字段
        dto.setName(model.getModelName()); // 确保名称正确

        // 格式化日期
        if (model.getCreateTime() != null) {
            dto.setCreationTime(model.getCreateTime().format(formatter));
        }
        if (model.getTrainTime() != null) {
            dto.setLastTrainingTime(model.getTrainTime().format(formatter));
        }

        // 转换模型类型
        dto.setType(model.getModelType() != null && model.getModelType() == 2 ? "数据增强预测" : "预测");

        // 转换 status 枚举为描述性字符串
        if (model.getStatus() != null) {
            dto.setStatus(model.getStatus().getDescription());
        } else {
            // 如果数据库中的 status 是 NULL，给一个默认值
            dto.setStatus(model.getIsDeleted() == 1 ? "已删除" : "未知");
        }

        // modelDescribe 已经在 BeanUtils.copyProperties 中被复制
        // dto.setModelDescribe(model.getModelDescribe());

        // performanceChartPath 也需要映射
        dto.setPerformanceChartPath(model.getModelMetricsPhoto());

        // 新增的指标字段也应该被 BeanUtils.copyProperties 自动复制了
        // dto.setModelMetricsPs(model.getModelMetricsPs());
        // dto.setModelMetricsJiniOrigin(model.getModelMetricsJiniOrigin());
        // dto.setModelMetricsJiniNow(model.getModelMetricsJiniNow());

        // 用途字段 (usage) 不在DTO中，因为前端直接使用 modelDescribe
        // 如果仍然需要，可以按之前逻辑处理

        return dto;
    }

}