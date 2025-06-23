// com.projectwz.partsforecast.entity/PartsInfo.java
package com.projectwz.partsforecast.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

// 已移除 @Entity, @Table 等注解
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartsInfo {
    // 已移除 @Id, @Column, @ManyToOne, @JoinColumn 等注解
    private Integer id;
    private AgentInfo agentInfo;
    private String partName;
    private String partCode;
    private String partType;
    private String partSpec;

    // 已移除所有 @OneToMany 关联字段
}