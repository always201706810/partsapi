// com/projectwz/partsforecast/entity/DatasumEntry.java
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
public class DatasumEntry {
    // 已移除 @Id, @Column, @ManyToOne, @JoinColumn 等注解
    private Integer id;
    private AgentInfo agentInfo;
    private LocalDateTime timeIndex;
    private String partsName;
    private String partsIdString;
    private BigDecimal trainSaleNum;
    private BigDecimal trainSalePrice;
    private BigDecimal trainSugPrice;
    private BigDecimal trainBuyPrice;
    private BigDecimal trainStoreIn;
    private BigDecimal storeInPrice;
    private BigDecimal trainStoreOut;
    private BigDecimal storeOutPrice;
    private BigDecimal trainStackNum;
    private BigDecimal trainReturned;
    private BigDecimal trainBreakdown;
    private BigDecimal trainCar;
}