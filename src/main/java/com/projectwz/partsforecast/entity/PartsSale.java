// com/projectwz/partsforecast/entity/PartsSale.java
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
public class PartsSale {
    // 已移除 @Id, @Column, @ManyToOne, @JoinColumn 等注解
    private Integer id;
    private AgentInfo agentInfo;
    private PartsInfo partsInfo;
    private String salePartName;
    private BigDecimal salePartNum;
    private LocalDateTime saleDate;
    private BigDecimal salePartPrice;
}