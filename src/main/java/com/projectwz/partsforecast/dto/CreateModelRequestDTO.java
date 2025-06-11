package com.projectwz.partsforecast.dto;

import lombok.Data;
// 确保导入的是 jakarta.validation 包
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class CreateModelRequestDTO {

    @NotEmpty(message = "模型基础名称不能为空")
    private String baseName;

    @NotNull(message = "必须选择一个代理商数据集")
    @Min(value = 1, message = "代理商ID必须是有效的正整数") // 增加一个最小值为1的验证
    private Integer agentId;

    private String description;
}