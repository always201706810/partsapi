// com/projectwz/partsforecast/entity/PartsStore.java
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
public class PartsStore {
    // 已移除 @Id, @Column, @ManyToOne, @JoinColumn 等注解
    private Integer id;
    private AgentInfo agentInfo;
    private PartsInfo partsInfo;
    private String storePartName;
    private BigDecimal storePartNum;
    private BigDecimal storePartRate;
    private LocalDateTime storeTime;
}