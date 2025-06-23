// com/projectwz/partsforecast/entity/ForecastResult.java
package com.projectwz.partsforecast.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// 已移除 @Entity, @Table 等注解
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResult {
    // 已移除 @Id, @Column, @ManyToOne, @JoinColumn 等注解
    private Integer id;
    private AgentInfo agentInfo;
    private PartsInfo partsInfo;
    private ModelManage modelManage;
    private String modelName;
    private String partsName;
    private BigDecimal forecastResult;
    private LocalDateTime forecastTime;
    private LocalDateTime createTime;
}