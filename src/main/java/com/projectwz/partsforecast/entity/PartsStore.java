// com/projectwz/partsforecast/entity/PartsStore.java
package com.projectwz.partsforecast.entity;

//import jakarta.persistence.*;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parts_store") // 配件库存表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartsStore {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 主键

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentInfo agentInfo; // 代理商id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_part_id") // Matches parts_info.id
    private PartsInfo partsInfo; // 配件id

    @Column(name = "store_part_name") // Redundant
    private String storePartName; // 配件名

    @Column(name = "store_part_num")
    private BigDecimal storePartNum; // 库存量

    @Column(name = "store_part_rate")
    private BigDecimal storePartRate; // 库存周转率 (SQL decimal(10,0))

    @Column(name = "store_time")
    private LocalDateTime storeTime; // 创建日期
}