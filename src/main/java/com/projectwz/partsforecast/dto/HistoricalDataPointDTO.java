// com/projectwz/partsforecast/dto/HistoricalDataPointDTO.java
package com.projectwz.partsforecast.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalDataPointDTO {
    private LocalDateTime date;
    private BigDecimal value;
    private String dataType; // e.g., "actual_purchase", "forecasted_demand"
}