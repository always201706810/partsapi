package com.projectwz.partsforecast.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForecastPointDTO {
    private String timePointLabel;      // 例如 "未来1", "2025-06-01"
    private LocalDateTime forecastDateTime; // 实际的预测日期时间
    private BigDecimal predictedValue;    // 预测值
}