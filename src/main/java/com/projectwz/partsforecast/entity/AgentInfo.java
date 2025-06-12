// com/projectwz/partsforecast/entity/AgentInfo.java
package com.projectwz.partsforecast.entity;

//import jakarta.persistence.*;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "agent_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentInfo {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int, not auto-incremented by default in provided DDL
    @Column(name = "id")
    private Integer id; // 代理商ID (SQL is INT, not auto-inc)

    @Column(name = "agent_name")
    private String agentName; // 代理商名称

    @OneToMany(mappedBy = "agentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsInfo> partsInfos;

    @OneToMany(mappedBy = "agentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsSale> partsSales;

    @OneToMany(mappedBy = "agentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsPurchase> partsPurchases;

    @OneToMany(mappedBy = "agentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsFit> partsFits;

    @OneToMany(mappedBy = "agentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartsStore> partsStores;

    @OneToMany(mappedBy = "agentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DatasumEntry> datasumEntries;

    @OneToMany(mappedBy = "agentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForecastResult> forecastResults;
}