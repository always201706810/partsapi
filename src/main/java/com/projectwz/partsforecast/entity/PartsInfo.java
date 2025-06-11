// com/projectwz/partsforecast/entity/PartsInfo.java
package com.projectwz.partsforecast.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "parts_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartsInfo {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 配件ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentInfo agentInfo; // 代理商ID

    @Column(name = "part_name")
    private String partName; // 配件名称

    @Column(name = "part_code", unique = true)
    private String partCode; // 配件编码

    @Column(name = "part_type")
    private String partType; // 配件类别

    @Column(name = "part_spec")
    private String partSpec; // 规格型号

    @OneToMany(mappedBy = "partsInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsSale> partsSales;

    @OneToMany(mappedBy = "partsInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsPurchase> partsPurchases;

    @OneToMany(mappedBy = "partsInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsFit> partsFits;

    @OneToMany(mappedBy = "partsInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsStore> partsStores;

    @OneToMany(mappedBy = "partsInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainInfo> trainInfos;

    @OneToMany(mappedBy = "partsInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForecastResult> forecastResults;
}