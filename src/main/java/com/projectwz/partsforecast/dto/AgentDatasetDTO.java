package com.projectwz.partsforecast.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDatasetDTO {
    private Integer id; // 代理商ID (对应 agent_info.id)
    private String name; // 数据集名称 (例如 "XX代理商数据集"，可以就是 agent_info.agent_name)
}