// com/projectwz/partsforecast/entity/TrainInfo.java
package com.projectwz.partsforecast.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "train_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainInfo {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL PK is int
    @Column(name = "id")
    private Integer id; // 主键数据集版本

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private ModelManage modelManage; // 模型ID

    @Column(name = "model_name") // Redundant
    private String modelName; // 模型名

    @Column(name = "update_time")
    private LocalDateTime updateTime; // 训练时间

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parts_id") // Matches parts_info.id
    private PartsInfo partsInfo; // 配件编码 (SQL type int, name implies ID)

    @Column(name = "parts_name") // Redundant
    private String partsName; // 配件名

    @Column(name = "parts_len")
    private String partsLen; // 观测点长度 (SQL VARCHAR)
}