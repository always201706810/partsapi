// com/projectwz/partsforecast/dto/HistoricalAnalysisDTO.java
package com.projectwz.partsforecast.dto;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class HistoricalAnalysisDTO { // For 预测结果分析 [cite: 15]
    private Integer partId;
    private String partName;
    private List<HistoricalDataPointDTO> dataPoints;
    // Could also include summary metrics like:
    // private BigDecimal averageDemandDifference;
    // private BigDecimal profitRateChange;
    // private BigDecimal salesAmountChange;
}