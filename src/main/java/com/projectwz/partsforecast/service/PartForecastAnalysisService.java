package com.projectwz.partsforecast.service;

import com.projectwz.partsforecast.dto.*;
import com.projectwz.partsforecast.entity.*;
// 已更改: 导入 Mappers
import com.projectwz.partsforecast.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartForecastAnalysisService {

    // 已更改: 注入 Mappers
    @Autowired private PartsInfoMapper partsInfoMapper;
    @Autowired private PartsStoreMapper partsStoreMapper;
    @Autowired private PartsFitMapper partsFitMapper;
    @Autowired private PartsSaleMapper partsSaleMapper;
    @Autowired private PartsPurchaseMapper partsPurchaseMapper;
    @Autowired private ForecastResultMapper forecastResultMapper;
    @Autowired private DatasumEntryMapper datasumEntryRepository;

    public List<CategoryTreeNodeDTO> getCategoryTree() {
        // 已更改: 调用 Mapper 方法
        List<PartsInfo> allParts = partsInfoMapper.findAll();
        Map<String, Set<String>> categoryMap = new HashMap<>();

        for (PartsInfo part : allParts) {
            String type = part.getPartType() != null ? part.getPartType() : "未分类";
            String spec = part.getPartSpec();
            categoryMap.putIfAbsent(type, new HashSet<>());
            if (spec != null && !spec.isEmpty()) {
                categoryMap.get(type).add(spec);
            }
        }

        List<CategoryTreeNodeDTO> tree = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : categoryMap.entrySet()) {
            String categoryLabel = entry.getKey();
            Set<String> specs = entry.getValue();
            List<CategoryTreeNodeDTO> children = new ArrayList<>();
            boolean effectiveChildrenFound = false;

            if (specs != null && !specs.isEmpty()) {
                for (String spec : specs) {
                    if (spec != null && !spec.isEmpty()) {
                        children.add(new CategoryTreeNodeDTO(spec, null, true, spec, categoryLabel, "subCategory"));
                        effectiveChildrenFound = true;
                    }
                }
            }
            CategoryTreeNodeDTO parentNode = new CategoryTreeNodeDTO(categoryLabel, children.isEmpty() ? null : children, !effectiveChildrenFound, categoryLabel, null, "category");
            tree.add(parentNode);
        }
        return tree;
    }

    public List<PartBasicInfoDTO> getPartsList(Integer agentId, String category, String subCategory, String nameFilter) {
        // 已更改: 使用 Map 传递动态查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", agentId);
        params.put("category", category);
        params.put("subCategory", subCategory);
        params.put("nameFilter", nameFilter);

        List<PartsInfo> partsEntities = partsInfoMapper.findAllWithFilter(params);
        System.out.println("Found " + partsEntities.size() + " parts entities after query for category=" + category + ", subCategory=" + subCategory);
        return partsEntities.stream().map(this::convertToPartBasicInfoDTO).collect(Collectors.toList());
    }

    private PartBasicInfoDTO convertToPartBasicInfoDTO(PartsInfo part) {
        PartBasicInfoDTO dto = new PartBasicInfoDTO();
        dto.setPartId(part.getId());
        dto.setCode(part.getPartCode());
        dto.setName(part.getPartName());
        dto.setCategory(part.getPartType());
        dto.setSubCategory(part.getPartSpec());

        // 已更改: 调用 Mapper 方法
        PartsStore latestStock = partsStoreMapper.findTopByPartsInfoIdOrderByStoreTimeDesc(part.getId());
        if (latestStock != null) {
            dto.setStock(latestStock.getStorePartNum() != null ? latestStock.getStorePartNum() : BigDecimal.ZERO);
            dto.setTurnoverRate(latestStock.getStorePartRate() != null ? latestStock.getStorePartRate().stripTrailingZeros().toPlainString() + "%" : "0%");
        } else {
            dto.setStock(BigDecimal.ZERO);
            dto.setTurnoverRate("0%");
        }

        List<PartsFit> fits = partsFitMapper.findByPartsInfoId(part.getId(), 1);
        if (!fits.isEmpty() && fits.get(0) != null) {
            dto.setProfit(fits.get(0).getFitPartPrice() != null ? fits.get(0).getFitPartPrice() : BigDecimal.ZERO);
            dto.setProfitRate(fits.get(0).getFitPartRate() != null ? fits.get(0).getFitPartRate().stripTrailingZeros().toPlainString() + "%" : "0%");
        } else {
            dto.setProfit(BigDecimal.ZERO);
            dto.setProfitRate("0%");
        }

        List<PartsSale> sales = partsSaleMapper.findByPartsInfoIdOrderBySaleDateDesc(part.getId(), 1);
        dto.setSalePrice(!sales.isEmpty() && sales.get(0) != null && sales.get(0).getSalePartPrice() != null ? sales.get(0).getSalePartPrice() : BigDecimal.ZERO);

        List<PartsPurchase> purchases = partsPurchaseMapper.findByPartsInfoIdOrderByBuyDateDesc(part.getId(), 1);
        dto.setPurchasePrice(!purchases.isEmpty() && purchases.get(0) != null && purchases.get(0).getBuyPartPrice() != null ? purchases.get(0).getBuyPartPrice() : BigDecimal.ZERO);

        dto.setRevenue(BigDecimal.ZERO);

        List<ForecastResult> forecasts = forecastResultMapper.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
                part.getId(),
                LocalDateTime.now().minusDays(1),
                5
        );

        dto.setWeek1((forecasts.size() > 0 && forecasts.get(0) != null) ? forecasts.get(0).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek2((forecasts.size() > 1 && forecasts.get(1) != null) ? forecasts.get(1).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek3((forecasts.size() > 2 && forecasts.get(2) != null) ? forecasts.get(2).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek4((forecasts.size() > 3 && forecasts.get(3) != null) ? forecasts.get(3).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek5((forecasts.size() > 4 && forecasts.get(4) != null) ? forecasts.get(4).getForecastResult() : BigDecimal.ZERO);

        return dto;
    }

    public PartForecastDetailDTO getPartForecastDetail(String partIdOrCode, int numForecastPoints) {
        Optional<PartsInfo> partInfoOptional = findPartsInfoByIdOrCode(partIdOrCode);

        if (!partInfoOptional.isPresent()) {
            return null;
        }
        PartsInfo part = partInfoOptional.get();

        PartForecastDetailDTO detailDTO = new PartForecastDetailDTO();
        detailDTO.setPartId(part.getId());
        detailDTO.setPartCode(part.getPartCode());
        detailDTO.setPartName(part.getPartName());

        List<ForecastResult> forecastResults = forecastResultMapper.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
                part.getId(),
                LocalDateTime.now().minusDays(1),
                numForecastPoints
        );

        List<ForecastPointDTO> forecastDemands = forecastResults.stream()
                .map(fr -> new ForecastPointDTO(
                        "未来 " + fr.getForecastTime().toLocalDate().toString(),
                        fr.getForecastTime(),
                        fr.getForecastResult()
                ))
                .collect(Collectors.toList());
        detailDTO.setForecastDemands(forecastDemands);

        return detailDTO;
    }

    public PartAnalysisDataDTO getPartAnalysisData(String partIdentifier, int historyMonths, int futureForecastPeriods) {
        Optional<PartsInfo> partInfoOptional = findPartsInfoByIdOrCode(partIdentifier);
        if (!partInfoOptional.isPresent()) {
            return null;
        }
        PartsInfo part = partInfoOptional.get();

        PartAnalysisDataDTO analysisDTO = new PartAnalysisDataDTO();
        analysisDTO.setPartId(part.getId());
        analysisDTO.setPartCode(part.getPartCode());
        analysisDTO.setPartName(part.getPartName());

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(historyMonths);

        // 1. 需求趋势
        PartAnalysisDataDTO.TimeSeriesDataDTO demandData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<DatasumEntry> historicalSalesData = datasumEntryRepository.findByPartsIdStringAndTimeIndexBetweenOrderByTimeIndexAsc(part.getPartCode(), startDate, endDate);
        List<String> demandTimeLabels = historicalSalesData.stream().map(e -> e.getTimeIndex().toLocalDate().toString()).collect(Collectors.toList());
        List<BigDecimal> actualDemandValues = historicalSalesData.stream().map(DatasumEntry::getTrainSaleNum).collect(Collectors.toList());
        demandData.setTimeLabels(demandTimeLabels);
        demandData.setActualValues(actualDemandValues);

        List<ForecastResult> forecastResults = forecastResultMapper.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(part.getId(), endDate.minusDays(1), futureForecastPeriods);
        demandData.setForecastValues(forecastResults.stream().map(ForecastResult::getForecastResult).collect(Collectors.toList()));
        analysisDTO.setDemandTrend(demandData);

        // 2. 库存历史
        PartAnalysisDataDTO.TimeSeriesDataDTO stockData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<DatasumEntry> historicalStockData = datasumEntryRepository.findByPartsIdStringAndTimeIndexBetweenOrderByTimeIndexAsc(part.getPartCode(), startDate, endDate);
        stockData.setTimeLabels(historicalStockData.stream().map(e -> e.getTimeIndex().toLocalDate().toString()).collect(Collectors.toList()));
        stockData.setActualValues(historicalStockData.stream().map(DatasumEntry::getTrainStackNum).collect(Collectors.toList()));
        analysisDTO.setStockHistory(stockData);

        // 3. 利润历史
        PartAnalysisDataDTO.TimeSeriesDataDTO profitData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<PartsFit> historicalProfitData = partsFitMapper.findByPartsInfoIdAndFitDateBetweenOrderByFitDateAsc(part.getId(), startDate, endDate);
        profitData.setTimeLabels(historicalProfitData.stream().map(e -> e.getFitDate().toLocalDate().toString()).collect(Collectors.toList()));
        profitData.setActualValues(historicalProfitData.stream().map(PartsFit::getFitPartPrice).collect(Collectors.toList()));
        analysisDTO.setProfitHistory(profitData);

        // 4. 销售额历史
        PartAnalysisDataDTO.TimeSeriesDataDTO salesAmountData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<PartsSale> historicalSalesRecords = partsSaleMapper.findByPartsInfoIdAndSaleDateBetweenOrderBySaleDateAsc(part.getId(), startDate, endDate);
        salesAmountData.setTimeLabels(historicalSalesRecords.stream().map(e -> e.getSaleDate().toLocalDate().toString()).collect(Collectors.toList()));
        salesAmountData.setActualValues(historicalSalesRecords.stream().map(e -> e.getSalePartPrice().multiply(e.getSalePartNum())).collect(Collectors.toList()));
        analysisDTO.setSalesAmountHistory(salesAmountData);

        return analysisDTO;
    }

    private Optional<PartsInfo> findPartsInfoByIdOrCode(String identifier) {
        try {
            Integer partId = Integer.parseInt(identifier);
            // 已更改: 调用 Mapper 方法
            return partsInfoMapper.findById(partId);
        } catch (NumberFormatException e) {
            // 已更改: 调用 Mapper 方法
            return partsInfoMapper.findByPartCode(identifier);
        }
    }
}