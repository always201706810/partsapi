package com.projectwz.partsforecast.dto;

import lombok.Data;
import java.util.List;
// 可以不继承 PartBasicInfoDTO，而是独立包含所需字段，或者前端自行合并信息
// 这里假设它包含了绘制图表所需的核心信息

@Data
public class PartForecastDetailDTO {
    private Integer partId;             // 配件ID
    private String partCode;            // 配件编码
    private String partName;            // 配件名称
    // 可以按需加入 category, subCategory 等基础信息
    private List<ForecastPointDTO> forecastDemands; // 详细的未来预测点列表
}