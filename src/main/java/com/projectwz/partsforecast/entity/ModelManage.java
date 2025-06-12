package com.projectwz.partsforecast.entity;

import javax.persistence.*;
//import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "model_manage")
@Data
public class ModelManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <--- 添加这个注解
    @Column(name = "id")
    private Integer id;

    @Column(name = "model_name")
    private String modelName;

    // --- 新增关联 ---
    @ManyToOne(fetch = FetchType.LAZY) // 使用懒加载提高性能
    @JoinColumn(name = "agent_id") // 指定数据库中的外键列名
    private AgentInfo agentInfo;
    @Column(name = "train_time")
    private LocalDateTime trainTime;

    @Column(name = "model_type")
    private Integer modelType;

    @Column(name = "model_describe")
    private String modelDescribe;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "model_metrics_mae")
    private BigDecimal modelMetricsMae;

    @Column(name = "model_metrics_mse")
    private BigDecimal modelMetricsMse;

    @Column(name = "model_metrics_fid")
    private BigDecimal modelMetricsFid;

    // --- 新增字段 ---
    @Column(name = "model_metrics_ps")
    private BigDecimal modelMetricsPs;

    @Column(name = "model_metrics_jini_origin")
    private BigDecimal modelMetricsJiniOrigin;

    @Column(name = "model_metrics_jini_now")
    private BigDecimal modelMetricsJiniNow;

    @Column(name = "model_metrics_photo")
    private String modelMetricsPhoto;

    @Enumerated(EnumType.ORDINAL) // 将枚举的顺序 (0, 1, 2, 3) 存入数据库的 INT 字段
    @Column(name = "status")
    private ModelStatus status;
}