// com/projectwz/partsforecast/entity/ForecastResult.java
package com.projectwz.partsforecast.entity;

//import jakarta.persistence.*;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "forecast_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResult {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 预测结果id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentInfo agentInfo; // 代理商ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parts_id")
    private PartsInfo partsInfo; // 配件ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private ModelManage modelManage; // 预测模型id

    @Column(name = "model_name") // Redundant if ModelManage is properly linked and fetched
    private String modelName; // 模型名称

    @Column(name = "parts_name") // Redundant if PartsInfo is properly linked and fetched
    private String partsName; // 预测配件名

    @Column(name = "forecast_result")
    private BigDecimal forecastResult; // 预测结果 (SQL decimal(12,0))

    @Column(name = "forecast_time")
    private LocalDateTime forecastTime; // 需求预测时间

    @Column(name = "create_time")
    private LocalDateTime createTime; // 创建时间
}