package com.projectwz.partsforecast.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelManageInfoDTO {
    private Integer id;
    private String name;
    private String creationTime;
    private String lastTrainingTime;
    private String type;
    private String modelDescribe; // 用于前端“用途”列
    private String status;

    // --- 评估指标 ---
    private BigDecimal modelMetricsMae;
    private BigDecimal modelMetricsMse;
    private BigDecimal modelMetricsFid;
    private BigDecimal modelMetricsPs;          // 新增
    private BigDecimal modelMetricsJiniOrigin;  // 新增
    private BigDecimal modelMetricsJiniNow;     // 新增
    private String performanceChartPath;

    private Integer agentId; // 用于创建模型时关联代理商
}