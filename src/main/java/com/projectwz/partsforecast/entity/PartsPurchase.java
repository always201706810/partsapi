// com/projectwz/partsforecast/entity/PartsPurchase.java
package com.projectwz.partsforecast.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parts_purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartsPurchase {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 主键

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentInfo agentInfo; // 代理商id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_part_id") // Matches parts_info.id
    private PartsInfo partsInfo; // 配件id

    @Column(name = "buy_part_name") // Redundant
    private String buyPartName; // 配件名

    @Column(name = "buy_part_num")
    private BigDecimal buyPartNum; // 采购数量

    @Column(name = "buy_date")
    private LocalDateTime buyDate; // 采购日期

    @Column(name = "buy_part_price")
    private BigDecimal buyPartPrice; // 采购价格
}