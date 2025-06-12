// com/projectwz/partsforecast/entity/DatasumEntry.java
package com.projectwz.partsforecast.entity;

import javax.persistence.*;
//import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "datasum")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasumEntry {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 主键id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentInfo agentInfo; // 代理商id

    @Column(name = "time_index")
    private LocalDateTime timeIndex; // 时间点

    @Column(name = "parts_name")
    private String partsName; // 配件名

    @Column(name = "parts_id")
    private String partsIdString; // 配件id (SQL is VARCHAR) - Storing as String

    @Column(name = "train_sale_num")
    private BigDecimal trainSaleNum; // 销售量

    @Column(name = "tran_sale_price") // Typo in SQL was tran_sale_price
    private BigDecimal trainSalePrice; // 销售价格

    @Column(name = "train_sug_price")
    private BigDecimal trainSugPrice; // 建议零售价

    @Column(name = "train_buy_price")
    private BigDecimal trainBuyPrice; // 采购价格

    @Column(name = "train_store_in")
    private BigDecimal trainStoreIn; // 入库数量

    @Column(name = "store_in_price")
    private BigDecimal storeInPrice; // 入库价格

    @Column(name = "train_store_out")
    private BigDecimal trainStoreOut; // 出库数量

    @Column(name = "store_out_price")
    private BigDecimal storeOutPrice; // 出库价格

    @Column(name = "train_stack_num")
    private BigDecimal trainStackNum; // 库存量

    @Column(name = "train_returned")
    private BigDecimal trainReturned; // 退货量

    @Column(name = "train_breakdown")
    private BigDecimal trainBreakdown; // 故障量

    @Column(name = "train_car")
    private BigDecimal trainCar; // 整车保有量
}