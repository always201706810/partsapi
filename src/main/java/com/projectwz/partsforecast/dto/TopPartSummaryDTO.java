// com.projectwz.partsforecast.dto.TopPartSummaryDTO
package com.projectwz.partsforecast.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List; // 添加这个为了包含详细的未来预测点

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopPartSummaryDTO {
    private String partName;
    private BigDecimal value; // 代表总销售量、总利润额或周转率
    private BigDecimal nextForecastValue; // 下一个时间点的预测需求量 (对应前端的 nextForecast)
    private List<BigDecimal> forecastTrend; // 新增：用于图表的未来多个预测点 (对应前端的 forecast 数组)
    private Integer partId; // 新增：方便前端可能需要的进一步操作
}