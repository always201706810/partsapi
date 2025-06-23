// com/projectwz/partsforecast/entity/TrainInfo.java
package com.projectwz.partsforecast.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// 已移除 @Entity, @Table 等注解
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainInfo {
    // 已移除 @Id, @Column, @ManyToOne, @JoinColumn 等注解
    private Integer id;
    private ModelManage modelManage;
    private String modelName;
    private LocalDateTime updateTime;
    private PartsInfo partsInfo;
    private String partsName;
    private String partsLen;
}