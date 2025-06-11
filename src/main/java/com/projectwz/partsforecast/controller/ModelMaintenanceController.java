package com.projectwz.partsforecast.controller;

import com.projectwz.partsforecast.dto.AgentDatasetDTO;
import com.projectwz.partsforecast.dto.ModelManageInfoDTO;
import com.projectwz.partsforecast.dto.PartsStatsDTO;
import com.projectwz.partsforecast.dto.CreateModelRequestDTO; // 新增
import com.projectwz.partsforecast.entity.ModelStatus;
import com.projectwz.partsforecast.service.ModelMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // 导入 @Valid
import org.slf4j.Logger; // 导入日志库
import org.slf4j.LoggerFactory; // 导入日志库

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models") // 模型维护相关的 API 基础路径
public class ModelMaintenanceController {

    @Autowired
    private ModelMaintenanceService modelMaintenanceService;
    // 添加一个日志记录器
    private static final Logger log = LoggerFactory.getLogger(ModelMaintenanceController.class);

    // --- 之前已有的接口 ---
    @GetMapping("/datasets")
    public ResponseEntity<List<AgentDatasetDTO>> getAvailableDatasets() {
        List<AgentDatasetDTO> datasets = modelMaintenanceService.getAllAgentDatasets();
        if (datasets == null || datasets.isEmpty()) {
            System.out.println("Backend: getAvailableDatasets is returning no datasets or null."); // 添加日志
            return ResponseEntity.noContent().build(); // 或者返回一个空的成功响应
        }
        System.out.println("Backend: getAvailableDatasets is returning " + datasets.size() + " datasets."); // 添加日志
        return ResponseEntity.ok(datasets);
    }

    @GetMapping("/datasets/{agentId}/stats")
    public ResponseEntity<PartsStatsDTO> getDatasetStats(@PathVariable Integer agentId) {
        PartsStatsDTO stats = modelMaintenanceService.getPartsStatsByAgentId(agentId);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    // --- 新增和完善的接口 ---

    /**
     * 获取模型列表 (支持筛选)
     */
    @GetMapping
    public ResponseEntity<List<ModelManageInfoDTO>> getAllModels(
            @RequestParam(required = false) String nameQuery,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime creationEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime trainingStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime trainingEnd
    ) {
        List<ModelManageInfoDTO> models = modelMaintenanceService.searchModels(nameQuery, creationStart, creationEnd, trainingStart, trainingEnd);
        return ResponseEntity.ok(models);
    }

    /**
     * 创建新模型 (基于基础名称和代理商，自动创建两种类型)
     */
    @PostMapping
    public ResponseEntity<List<ModelManageInfoDTO>> createNewModels(@RequestBody CreateModelRequestDTO createRequest) {
        System.out.println("成功进入 Controller 方法！接收到的 agentId: " + createRequest.getAgentId());
        try {
            // 调用修改后的 Service 方法
            List<ModelManageInfoDTO> createdModels = modelMaintenanceService.createModelsForAgent(createRequest);
            // Service方法会立即返回，同时后台异步调用Flask
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdModels); // 使用 202 Accepted 状态码表示请求已接受，正在处理
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // 或者返回更详细的错误信息
        }
    }




    @PostMapping("/test-create")
    public ResponseEntity<String> testCreateModel(@RequestBody String rawPayload) {
        // 使用日志记录器打印接收到的最原始的请求体字符串
        log.info("接收到 /test-create 请求，原始 Payload 字符串: {}", rawPayload);

        // 您可以在这里尝试手动解析，看看会不会出错
        try {
            // ObjectMapper objectMapper = new ObjectMapper();
            // CreateModelRequestDTO dto = objectMapper.readValue(rawPayload, CreateModelRequestDTO.class);
            // log.info("手动解析成功: {}", dto);
        } catch (Exception e) {
            log.error("手动解析JSON失败!", e);
        }

        // 无论如何都返回一个成功的响应
        return ResponseEntity.ok("后端已收到您的测试请求，请查看控制台日志。");
    }



    /**
     * 获取单个模型详情
     */
    @GetMapping("/{modelId}")
    public ResponseEntity<ModelManageInfoDTO> getModelDetails(@PathVariable Integer modelId) {
        try {
            ModelManageInfoDTO model = modelMaintenanceService.getModelDetails(modelId);
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 重新训练模型
     */
    @PostMapping("/{modelId}/retrain")
    public ResponseEntity<ModelManageInfoDTO> retrainModel(@PathVariable Integer modelId /*, @RequestBody(required=false) RetrainParamsDTO params */) {
        // RetrainParamsDTO 可以包含触发Flask需要的额外参数
        try {
            ModelManageInfoDTO retrainedModel = modelMaintenanceService.retrainModel(modelId);
            return ResponseEntity.ok(retrainedModel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除模型
     */
    @DeleteMapping("/{modelId}")
    public ResponseEntity<Void> deleteModel(@PathVariable Integer modelId) {
        try {
            modelMaintenanceService.deleteModel(modelId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ...
    /**
     * 用于接收来自 Flask 服务的训练状态更新回调
     */
    @PutMapping("/{modelId}/status") // 使用 PUT 方法更新资源状态
    public ResponseEntity<Void> updateModelStatus(@PathVariable Integer modelId, @RequestBody Map<String, Object> payload) {
        try {
            String newStatusStr = (String) payload.get("status");
            String errorMessage = (String) payload.get("errorMessage");

            // 将字符串状态转换为枚举
            ModelStatus newStatus = ModelStatus.valueOf(newStatusStr.toUpperCase()); // "COMPLETED" -> ModelStatus.COMPLETED

            modelMaintenanceService.updateModelStatus(modelId, newStatus, errorMessage);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 如果状态转换失败或更新失败
            System.err.println("更新模型状态失败: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}