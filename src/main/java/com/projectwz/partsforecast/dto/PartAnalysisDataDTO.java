package com.projectwz.partsforecast.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PartAnalysisDataDTO {
    private Integer partId;
    private String partCode;
    private String partName;

    private TimeSeriesDataDTO demandTrend;
    private TimeSeriesDataDTO stockHistory;
    private TimeSeriesDataDTO profitHistory;
    private TimeSeriesDataDTO salesAmountHistory;

    @Data
    public static class TimeSeriesDataDTO {
        private List<String> timeLabels; // X轴的时间/周期标签 (例如 "YYYY-MM-DD")
        private List<BigDecimal> actualValues;
        private List<BigDecimal> forecastValues; // 仅用于需求趋势图的预测线
    }
}