// com/projectwz/partsforecast/dto/DashboardOverviewDTO.java
package com.projectwz.partsforecast.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardOverviewDTO {
    private Long totalPartsCount; // 总配件数 [cite: 3]
    private List<TopPartSummaryDTO> topSellingParts; // 销售Top配件 [cite: 3]
    private List<TopPartSummaryDTO> topProfitableParts; // 利润Top配件 [cite: 3]
    private List<TopPartSummaryDTO> topTurnoverParts; // 周转率Top配件 (implied by "库存周转" [cite: 1] and Figure 5-5 [cite: 8])

}