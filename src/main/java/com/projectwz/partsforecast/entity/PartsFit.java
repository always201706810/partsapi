// com/projectwz/partsforecast/entity/PartsFit.java
package com.projectwz.partsforecast.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parts_fit") // 配件利润表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartsFit {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 主键

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentInfo agentInfo; // 代理商id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fit_part_id") // Matches parts_info.id
    private PartsInfo partsInfo; // 配件id

    @Column(name = "fit_part_name") // Redundant
    private String fitPartName; // 配件名

    @Column(name = "fit_part_price")
    private BigDecimal fitPartPrice; // 利润金额

    @Column(name = "fit_part_rate")
    private BigDecimal fitPartRate; // 利润率 (SQL decimal(10,0))

    @Column(name = "fit_date")
    private LocalDateTime fitDate; // 创建日期
}