package com.projectwz.partsforecast.service;

import com.projectwz.partsforecast.dto.*;
import com.projectwz.partsforecast.entity.*;
import com.projectwz.partsforecast.repository.*; // 引入所有需要的 Repository
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

//import jakarta.persistence.criteria.Predicate; // JPA Criteria API
import javax.persistence.criteria.Predicate; // JPA Criteria API
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PartForecastAnalysisService {

    @Autowired private PartsInfoRepository partsInfoRepository;
    @Autowired private PartsStoreRepository partsStoreRepository;
    @Autowired private PartsFitRepository partsFitRepository;
    @Autowired private PartsSaleRepository partsSaleRepository;
    @Autowired private PartsPurchaseRepository partsPurchaseRepository;
    @Autowired private ForecastResultRepository forecastResultRepository;
    @Autowired private DatasumEntryRepository datasumEntryRepository;

    // 获取分类树
    public List<CategoryTreeNodeDTO> getCategoryTree() {
        List<PartsInfo> allParts = partsInfoRepository.findAll();
        // 使用 Map<String, Set<String>> 来存储 category -> set of subCategories
        Map<String, Set<String>> categoryMap = new HashMap<>();

        for (PartsInfo part : allParts) {
            String type = part.getPartType() != null ? part.getPartType() : "未分类";
            String spec = part.getPartSpec(); // spec 可以为 null 或空

            // 确保即使 spec 为 null 或空，父分类也会被记录
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
            boolean effectiveChildrenFound = false; // 用于判断父节点是否真的有有效子节点

            if (specs != null && !specs.isEmpty()) {
                for (String spec : specs) {
                    if (spec != null && !spec.isEmpty()) {
                        // 子节点：isLeaf 应该为 true
                        children.add(new CategoryTreeNodeDTO(spec, null, true, spec, categoryLabel, "subCategory")); // <--- 确保这里 isLeaf 是 true
                        effectiveChildrenFound = true;
                    }
                }
            }

            // 父节点：isLeaf 取决于是否有有效子节点
            CategoryTreeNodeDTO parentNode = new CategoryTreeNodeDTO(
                    categoryLabel,
                    children.isEmpty() ? null : children,
                    !effectiveChildrenFound, // 如果没有有效子节点，则父节点是叶子 (isLeaf = true)
                    // 如果有有效子节点，则父节点不是叶子 (isLeaf = false)
                    categoryLabel,
                    null,
                    "category"
            );
            tree.add(parentNode);
        }
        return tree;
    }

    // 获取配件列表 (修改以匹配前端DTO字段名)
    public List<PartBasicInfoDTO> getPartsList(Integer agentId, String category, String subCategory, String nameFilter) {
        System.out.println("Backend getPartsList called with: agentId=" + agentId +
                ", category=" + category + // 父分类
                ", subCategory=" + subCategory + // 子分类
                ", nameFilter=" + nameFilter);

        Specification<PartsInfo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentInfo").get("id"), agentId));
            }
            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("partType"), category)); // 匹配一级分类
                System.out.println("Filtering by partType: " + category);
            }
            if (subCategory != null && !subCategory.isEmpty()) {
                // 如果传了 subCategory，那 category 也应该有值 (除非设计允许单独按 subCategory 查)
                predicates.add(cb.equal(root.get("partSpec"), subCategory)); // 匹配二级分类
                System.out.println("Filtering by partSpec: " + subCategory);
            }
            if (nameFilter != null && !nameFilter.isEmpty()){
                predicates.add(cb.like(root.get("partName"), "%" + nameFilter + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<PartsInfo> partsEntities = partsInfoRepository.findAll(spec);
        // ...
        System.out.println("Found " + partsEntities.size() + " parts entities after query for category=" + category + ", subCategory=" + subCategory);

        return partsEntities.stream().map(this::convertToPartBasicInfoDTO).collect(Collectors.toList());
    }

    // 辅助方法：将 PartsInfo 实体转换为 PartBasicInfoDTO
    private PartBasicInfoDTO convertToPartBasicInfoDTO(PartsInfo part) {
        PartBasicInfoDTO dto = new PartBasicInfoDTO();
        dto.setPartId(part.getId());
        dto.setCode(part.getPartCode());   // 对应前端 code
        dto.setName(part.getPartName());   // 对应前端 name
        dto.setCategory(part.getPartType()); // 对应前端 category
        dto.setSubCategory(part.getPartSpec());// 对应前端 subCategory

        // 获取库存等信息
        PartsStore latestStock = partsStoreRepository.findTopByPartsInfoIdOrderByStoreTimeDesc(part.getId());
        if (latestStock != null) {
            dto.setStock(latestStock.getStorePartNum() != null ? latestStock.getStorePartNum() : BigDecimal.ZERO);
            dto.setTurnoverRate(latestStock.getStorePartRate() != null ? latestStock.getStorePartRate().stripTrailingZeros().toPlainString() + "%" : "0%");
        } else {
            dto.setStock(BigDecimal.ZERO);
            dto.setTurnoverRate("0%");
        }

        // 获取利润信息 (简化为最新一条，实际可能需要聚合)
        List<PartsFit> fits = partsFitRepository.findByPartsInfoId(part.getId(), PageRequest.of(0,1)); // 假设有这个方法
        if (!fits.isEmpty() && fits.get(0) != null) {
            dto.setProfit(fits.get(0).getFitPartPrice() != null ? fits.get(0).getFitPartPrice() : BigDecimal.ZERO);
            dto.setProfitRate(fits.get(0).getFitPartRate() != null ? fits.get(0).getFitPartRate().stripTrailingZeros().toPlainString() + "%" : "0%");
        } else {
            dto.setProfit(BigDecimal.ZERO);
            dto.setProfitRate("0%");
        }

        // 获取最新售价和采购价 (简化)
        List<PartsSale> sales = partsSaleRepository.findByPartsInfoIdOrderBySaleDateDesc(part.getId(), PageRequest.of(0,1));
        dto.setSalePrice(!sales.isEmpty() && sales.get(0) != null && sales.get(0).getSalePartPrice() != null ? sales.get(0).getSalePartPrice() : BigDecimal.ZERO);

        List<PartsPurchase> purchases = partsPurchaseRepository.findByPartsInfoIdOrderByBuyDateDesc(part.getId(), PageRequest.of(0,1));
        dto.setPurchasePrice(!purchases.isEmpty() && purchases.get(0) != null && purchases.get(0).getBuyPartPrice() != null ? purchases.get(0).getBuyPartPrice() : BigDecimal.ZERO);

        // 销售额 (revenue) 需要根据历史销售聚合，这里先设为0或根据最新售价*库存估算（不准确）
        // 假设 revenue 是一个需要计算的字段，这里可以从JSON数据中直接取（如果后端能存的话），或者前端自己根据其他数据计算
        // 此处先简单处理，如果你的parts_info表有revenue字段，可以用 part.getRevenue()
        dto.setRevenue(BigDecimal.ZERO); // 实际应有计算逻辑或从数据库获取


        // 填充 week1-week5 预测数据
        List<ForecastResult> forecasts = forecastResultRepository.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
                part.getId(),
                LocalDateTime.now().minusDays(1), // 获取包含今天的未来预测
                PageRequest.of(0, 5)
        );

        dto.setWeek1((forecasts.size() > 0 && forecasts.get(0) != null) ? forecasts.get(0).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek2((forecasts.size() > 1 && forecasts.get(1) != null) ? forecasts.get(1).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek3((forecasts.size() > 2 && forecasts.get(2) != null) ? forecasts.get(2).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek4((forecasts.size() > 3 && forecasts.get(3) != null) ? forecasts.get(3).getForecastResult() : BigDecimal.ZERO);
        dto.setWeek5((forecasts.size() > 4 && forecasts.get(4) != null) ? forecasts.get(4).getForecastResult() : BigDecimal.ZERO);

        return dto;
    }

    // 获取单个配件的详细预测 (用于图表)
    public PartForecastDetailDTO getPartForecastDetail(String partIdOrCode, int numForecastPoints) {
        Optional<PartsInfo> partInfoOptional;
        // 尝试按ID查找
        try {
            Integer partId = Integer.parseInt(partIdOrCode);
            partInfoOptional = partsInfoRepository.findById(partId);
        } catch (NumberFormatException e) {
            // 如果不是数字，则按Code查找
            partInfoOptional = partsInfoRepository.findByPartCode(partIdOrCode);
        }

//        if (partInfoOptional.isEmpty()) {
        if (!partInfoOptional.isPresent()) {
            return null; // 或者抛出 PartNotFoundException
        }
        PartsInfo part = partInfoOptional.get();

        PartForecastDetailDTO detailDTO = new PartForecastDetailDTO();
        detailDTO.setPartId(part.getId());
        detailDTO.setPartCode(part.getPartCode());
        detailDTO.setPartName(part.getPartName());
//        detailDTO.setCategory(part.getPartType());
//        detailDTO.setSubCategory(part.getPartSpec());
        // ... 填充其他基础信息 ...

        List<ForecastResult> forecastResults = forecastResultRepository.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
                part.getId(),
                LocalDateTime.now().minusDays(1), // 获取包含今天的未来预测
                PageRequest.of(0, numForecastPoints)
        );

        List<ForecastPointDTO> forecastDemands = forecastResults.stream()
                .map(fr -> new ForecastPointDTO(
                        "未来 " + fr.getForecastTime().toLocalDate().toString(), // 或者更友好的标签
                        fr.getForecastTime(),
                        fr.getForecastResult()
                ))
                .collect(Collectors.toList());
        detailDTO.setForecastDemands(forecastDemands);

        return detailDTO;
    }


// com/projectwz/partsforecast/service/PartForecastAnalysisService.java
// ... (确保所有需要的 Repository 都已注入，包括 DatasumEntryRepository, PartsFitRepository, PartsSaleRepository) ...

    public PartAnalysisDataDTO getPartAnalysisData(String partIdentifier, int historyMonths, int futureForecastPeriods) {
        Optional<PartsInfo> partInfoOptional = findPartsInfoByIdOrCode(partIdentifier); // 你需要实现这个辅助方法
//        if (partInfoOptional.isEmpty()) {
        if (!partInfoOptional.isPresent()) {
            return null; // 或者抛出异常
        }
        PartsInfo part = partInfoOptional.get();

        PartAnalysisDataDTO analysisDTO = new PartAnalysisDataDTO();
        analysisDTO.setPartId(part.getId());
        analysisDTO.setPartCode(part.getPartCode());
        analysisDTO.setPartName(part.getPartName());

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(historyMonths); // 获取历史数据的时间范围

        // 1. 需求趋势 (历史销量 vs 未来/历史预测)
        PartAnalysisDataDTO.TimeSeriesDataDTO demandData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<String> demandTimeLabels = new ArrayList<>();
        List<BigDecimal> actualDemandValues = new ArrayList<>();
        // 从 datasum 表获取历史销量 (假设 time_index 是日期，train_sale_num 是销量)
        // 你需要根据你的 datasum 结构来查询和聚合，例如按周或按月
        // 简化示例：假设 datasum 有对应时间点和销量
        List<DatasumEntry> historicalSalesData = datasumEntryRepository.findByPartsIdStringAndTimeIndexBetweenOrderByTimeIndexAsc(
                part.getPartCode(), // 假设 datasum.parts_id 存的是 part_code
                startDate,
                endDate
        ); // 你需要在 DatasumEntryRepository 中创建此方法
        for (DatasumEntry entry : historicalSalesData) {
            demandTimeLabels.add(entry.getTimeIndex().toLocalDate().toString()); // 或其他格式
            actualDemandValues.add(entry.getTrainSaleNum());
        }
        demandData.setTimeLabels(new ArrayList<>(demandTimeLabels)); // 拷贝一份作为基础时间标签
        demandData.setActualValues(actualDemandValues);

        // 获取未来预测销量 (或历史预测)
        List<ForecastResult> forecastResults = forecastResultRepository.findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
                part.getId(),
                endDate.minusDays(1), // 从昨天开始，包含今天的未来预测
                PageRequest.of(0, futureForecastPeriods)
        );
        List<BigDecimal> forecastDemandValues = forecastResults.stream()
                .map(ForecastResult::getForecastResult)
                .collect(Collectors.toList());
        // 如果需求趋势图的X轴需要包含未来预测的时间点，需要合并时间标签
        // 这里简化处理，假设前端会处理未来预测的时间点标签对齐
        demandData.setForecastValues(forecastDemandValues);
        analysisDTO.setDemandTrend(demandData);


        // 2. 库存历史
        PartAnalysisDataDTO.TimeSeriesDataDTO stockData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<String> stockTimeLabels = new ArrayList<>();
        List<BigDecimal> actualStockValues = new ArrayList<>();
        // 从 datasum 获取历史库存 (train_stack_num) 或 parts_store 的历史记录
        List<DatasumEntry> historicalStockData = datasumEntryRepository.findByPartsIdStringAndTimeIndexBetweenOrderByTimeIndexAsc(part.getPartCode(), startDate, endDate);
        for (DatasumEntry entry : historicalStockData) {
            stockTimeLabels.add(entry.getTimeIndex().toLocalDate().toString());
            actualStockValues.add(entry.getTrainStackNum());
        }
        stockData.setTimeLabels(stockTimeLabels);
        stockData.setActualValues(actualStockValues);
        analysisDTO.setStockHistory(stockData);


        // 3. 利润历史
        PartAnalysisDataDTO.TimeSeriesDataDTO profitData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<String> profitTimeLabels = new ArrayList<>();
        List<BigDecimal> actualProfitValues = new ArrayList<>();
        // 从 parts_fit 获取历史利润
        List<PartsFit> historicalProfitData = partsFitRepository.findByPartsInfoIdAndFitDateBetweenOrderByFitDateAsc(part.getId(), startDate, endDate);
        // 你需要在 PartsFitRepository 中创建此方法
        for (PartsFit entry : historicalProfitData) {
            profitTimeLabels.add(entry.getFitDate().toLocalDate().toString());
            actualProfitValues.add(entry.getFitPartPrice()); // 利润金额
        }
        profitData.setTimeLabels(profitTimeLabels);
        profitData.setActualValues(actualProfitValues);
        analysisDTO.setProfitHistory(profitData);


        // 4. 销售额历史
        PartAnalysisDataDTO.TimeSeriesDataDTO salesAmountData = new PartAnalysisDataDTO.TimeSeriesDataDTO();
        List<String> salesAmountTimeLabels = new ArrayList<>();
        List<BigDecimal> actualSalesAmountValues = new ArrayList<>();
        // 从 parts_sale 获取历史销售记录并计算销售额
        List<PartsSale> historicalSalesRecords = partsSaleRepository.findByPartsInfoIdAndSaleDateBetweenOrderBySaleDateAsc(part.getId(), startDate, endDate);
        // 你需要在 PartsSaleRepository 中创建此方法
        // 你可能需要按时间段聚合销售额 (例如按日、周、月)
        // 简化：假设每条记录就是一个时间点
        for (PartsSale entry : historicalSalesRecords) {
            salesAmountTimeLabels.add(entry.getSaleDate().toLocalDate().toString());
            actualSalesAmountValues.add(entry.getSalePartPrice().multiply(entry.getSalePartNum()));
        }
        salesAmountData.setTimeLabels(salesAmountTimeLabels);
        salesAmountData.setActualValues(actualSalesAmountValues);
        analysisDTO.setSalesAmountHistory(salesAmountData);


        return analysisDTO;
    }

    // 辅助方法，根据ID或Code查找PartsInfo
    private Optional<PartsInfo> findPartsInfoByIdOrCode(String identifier) {
        try {
            Integer partId = Integer.parseInt(identifier);
            return partsInfoRepository.findById(partId);
        } catch (NumberFormatException e) {
            return partsInfoRepository.findByPartCode(identifier);
        }
    }





}