// com/projectwz/partsforecast/entity/ModelManage.java
package com.projectwz.partsforecast.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// 已移除 @Entity, @Table 等注解
@Data
public class ModelManage {

    // 已移除 @Id, @GeneratedValue, @Column, @ManyToOne, @JoinColumn, @Enumerated 等注解
    private Integer id;
    private String modelName;
    private AgentInfo agentInfo;
    private LocalDateTime trainTime;
    private Integer modelType;
    private String modelDescribe;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private BigDecimal modelMetricsMae;
    private BigDecimal modelMetricsMse;
    private BigDecimal modelMetricsFid;
    private BigDecimal modelMetricsPs;
    private BigDecimal modelMetricsJiniOrigin;
    private BigDecimal modelMetricsJiniNow;
    private String modelMetricsPhoto;
    private ModelStatus status;
}