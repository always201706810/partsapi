// com/projectwz/partsforecast/entity/PartsSale.java
package com.projectwz.partsforecast.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parts_sale")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartsSale {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 主键

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentInfo agentInfo; // 代理商id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_part_id") // Matches parts_info.id
    private PartsInfo partsInfo; // 配件id

    @Column(name = "sale_part_name") // Redundant
    private String salePartName; // 配件名

    @Column(name = "sale_part_num")
    private BigDecimal salePartNum; // 销售数量

    @Column(name = "sale_date")
    private LocalDateTime saleDate; // 销售日期

    @Column(name = "sale_part_price")
    private BigDecimal salePartPrice; // 销售价格
}