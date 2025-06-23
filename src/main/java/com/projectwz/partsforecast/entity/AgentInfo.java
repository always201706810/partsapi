// com/projectwz/partsforecast/entity/AgentInfo.java
package com.projectwz.partsforecast.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

// 已移除 @Entity, @Table 等注解
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentInfo {
    // 已移除 @Id, @Column 等注解
    private Integer id;

    private String agentName;

    // 已移除所有 @OneToMany 关联字段
}