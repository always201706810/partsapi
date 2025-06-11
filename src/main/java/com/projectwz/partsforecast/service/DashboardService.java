// com/projectwz/partsforecast/service/DashboardService.java
package com.projectwz.partsforecast.service;

import com.projectwz.partsforecast.dto.DashboardOverviewDTO;
import com.projectwz.partsforecast.dto.TopPartSummaryDTO;
import com.projectwz.partsforecast.entity.ForecastResult;
import com.projectwz.partsforecast.entity.PartsInfo;
import com.projectwz.partsforecast.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired private PartsInfoRepository partsInfoRepository;
    @Autowired private PartsSaleRepository partsSaleRepository;
    @Autowired private PartsFitRepository partsFitRepository;
    @Autowired private PartsStoreRepository partsStoreRepository;
    @Autowired private ForecastResultRepository forecastResultRepository;

    public DashboardOverviewDTO getDashboardOverview(Integer agentId) {
        DashboardOverviewDTO overview = new DashboardOverviewDTO();
        overview.setTotalPartsCount(agentId == null ? partsInfoRepository.count() : partsInfoRepository.countByAgentInfoId(agentId));

        PageRequest topNPageable = PageRequest.of(0, 5); // Top 5

        List<Object[]> sellingResults = partsSaleRepository.findTopSellingParts(agentId, topNPageable);
        overview.setTopSellingParts(mapToTopPartSummaryDTO(sellingResults, "quantity"));

        List<Object[]> profitableResults = partsFitRepository.findTopProfitableParts(agentId, topNPageable);
        overview.setTopProfitableParts(mapToTopPartSummaryDTO(profitableResults, "profit"));

        List<Object[]> turnoverResults = partsStoreRepository.findTopTurnoverParts(agentId, topNPageable);
        overview.setTopTurnoverParts(mapToTopPartSummaryDTO(turnoverResults, "turnoverRate"));

        return overview;
    }

//    private List<TopPartSummaryDTO> mapToTopPartSummaryDTO(List<Object[]> results, String valueType) {
//        return results.stream().map(record -> {
//            String partName = (String) record[0];
//            BigDecimal value = record[1] != null ? new BigDecimal(record[1].toString()) : BigDecimal.ZERO;
//
//            // Fetch next forecast for this part (simplified - needs partId)
//            // This requires getting partId from partName first, which can be tricky if names are not unique.
//            // For now, placeholder.
//            BigDecimal nextForecast = getNextForecastByPartName(partName);
//
//            return new TopPartSummaryDTO(partName, value, nextForecast);
//        }).collect(Collectors.toList());
//    }

    private List<TopPartSummaryDTO> mapToTopPartSummaryDTO(List<Object[]> results, String valueType) {
        return results.stream().map(record -> {
            String partName = (String) record[0];
            // 假设 record[0] 是 partName, record[1] 是 partId (需要修改Repository查询以包含partId)
            // 或者在获取 partName 后，再通过 partName 查询 partId (如果name唯一)
            // 为了简化，我们先假设能拿到 partId
            Integer partId = null;
            // 示例：如果查询返回了 partId (例如在第二列)
            if (record.length > 2 && record[2] instanceof Number) { // 假设 partId 在第三个位置 (index 2)
                partId = ((Number) record[2]).intValue();
            } else {
                // 如果查询没有直接返回 partId，需要根据 partName 查询
                Optional<PartsInfo> piOpt = partsInfoRepository.findByPartNameContaining(partName).stream().findFirst();
                if (piOpt.isPresent()) {
                    partId = piOpt.get().getId();
                }
            }

            BigDecimal value = record[1] != null ? new BigDecimal(record[1].toString()) : BigDecimal.ZERO;
            BigDecimal nextForecast = BigDecimal.ZERO;
            List<BigDecimal> forecastTrend = new ArrayList<>();

            if (partId != null) {
                ForecastResult fr = forecastResultRepository.findTopByPartsInfoIdOrderByForecastTimeDesc(partId);
                if (fr != null) {
                    nextForecast = fr.getForecastResult();
                }
                // 获取未来多个预测点 (例如5个或12个)
                List<ForecastResult> futureForecasts = forecastResultRepository.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
                        partId,
                        LocalDateTime.now(), // 或者一个更精确的“未来”起始点
                        PageRequest.of(0, 12) // 获取12个点用于图表
                );
                forecastTrend = futureForecasts.stream().map(ForecastResult::getForecastResult).collect(Collectors.toList());
            }

            return new TopPartSummaryDTO(partName, value, nextForecast, forecastTrend, partId);
        }).collect(Collectors.toList());
    }




    private BigDecimal getNextForecastByPartName(String partName) {
        // Placeholder: ideally, query PartsInfo by name to get ID, then query ForecastResult
        // For simplicity, this is a very rough placeholder
        return partsInfoRepository.findByPartNameContaining(partName).stream()
                .findFirst()
                .map(partsInfo -> {
                    ForecastResult fr = forecastResultRepository.findTopByPartsInfoIdOrderByForecastTimeDesc(partsInfo.getId());
                    return fr != null ? fr.getForecastResult() : BigDecimal.ZERO;
                }).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getPartNextForecast(Integer partId){ // Used by controller for specific part's next forecast [cite: 9]
        ForecastResult fr = forecastResultRepository.findTopByPartsInfoIdOrderByForecastTimeDesc(partId);
        return fr != null ? fr.getForecastResult() : BigDecimal.ZERO;
    }
}