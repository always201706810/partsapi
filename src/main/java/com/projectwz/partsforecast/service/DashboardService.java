package com.projectwz.partsforecast.service;

import com.projectwz.partsforecast.dto.DashboardOverviewDTO;
import com.projectwz.partsforecast.dto.TopPartSummaryDTO;
import com.projectwz.partsforecast.entity.ForecastResult;
import com.projectwz.partsforecast.entity.PartsInfo;
// 已更改: 导入 Mappers
import com.projectwz.partsforecast.mapper.ForecastResultMapper;
import com.projectwz.partsforecast.mapper.PartsFitMapper;
import com.projectwz.partsforecast.mapper.PartsInfoMapper;
import com.projectwz.partsforecast.mapper.PartsSaleMapper;
import com.projectwz.partsforecast.mapper.PartsStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    // 已更改: 注入 Mappers
    @Autowired private PartsInfoMapper partsInfoMapper;
    @Autowired private PartsSaleMapper partsSaleMapper;
    @Autowired private PartsFitMapper partsFitMapper;
    @Autowired private PartsStoreMapper partsStoreMapper;
    @Autowired private ForecastResultMapper forecastResultMapper;

    public DashboardOverviewDTO getDashboardOverview(Integer agentId) {
        DashboardOverviewDTO overview = new DashboardOverviewDTO();

        // 已更改: 调用 Mapper 方法
        overview.setTotalPartsCount(agentId == null ? partsInfoMapper.countAll() : partsInfoMapper.countByAgentInfoId(agentId));

        int topN = 5; // 原 PageRequest.of(0, 5)

        // 已更改: 调用 Mapper 方法，传入 limit
        List<Map<String, Object>> sellingResults = partsSaleMapper.findTopSellingParts(agentId, topN);
        overview.setTopSellingParts(mapToTopPartSummaryDTO(sellingResults));

        List<Map<String, Object>> profitableResults = partsFitMapper.findTopProfitableParts(agentId, topN);
        overview.setTopProfitableParts(mapToTopPartSummaryDTO(profitableResults));

        List<Map<String, Object>> turnoverResults = partsStoreMapper.findTopTurnoverParts(agentId, topN);
        overview.setTopTurnoverParts(mapToTopPartSummaryDTO(turnoverResults));

        return overview;
    }

    // 已更改: 参数类型从 List<Object[]> 变为 List<Map<String, Object>>
    private List<TopPartSummaryDTO> mapToTopPartSummaryDTO(List<Map<String, Object>> results) {
        return results.stream().map(record -> {
            // 已更改: 从 Map 中按键名获取值
            String partName = (String) record.get("partName");
            Integer partId = ((Number) record.get("partId")).intValue();
            BigDecimal value = record.get("value") != null ? new BigDecimal(record.get("value").toString()) : BigDecimal.ZERO;

            BigDecimal nextForecast = BigDecimal.ZERO;
            List<BigDecimal> forecastTrend = new ArrayList<>();

            if (partId != null) {
                // 已更改: 调用 Mapper 方法
                ForecastResult fr = forecastResultMapper.findTopByPartsInfoIdOrderByForecastTimeDesc(partId);
                if (fr != null) {
                    nextForecast = fr.getForecastResult();
                }

                List<ForecastResult> futureForecasts = forecastResultMapper.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
                        partId,
                        LocalDateTime.now(),
                        12 // 获取12个点用于图表
                );
                forecastTrend = futureForecasts.stream().map(ForecastResult::getForecastResult).collect(Collectors.toList());
            }

            return new TopPartSummaryDTO(partName, value, nextForecast, forecastTrend, partId);
        }).collect(Collectors.toList());
    }

    public BigDecimal getPartNextForecast(Integer partId){
        // 已更改: 调用 Mapper 方法
        ForecastResult fr = forecastResultMapper.findTopByPartsInfoIdOrderByForecastTimeDesc(partId);
        return fr != null ? fr.getForecastResult() : BigDecimal.ZERO;
    }
}