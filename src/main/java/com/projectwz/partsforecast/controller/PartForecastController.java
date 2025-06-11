// com/projectwz/partsforecast/controller/PartForecastController.java
package com.projectwz.partsforecast.controller;

import com.projectwz.partsforecast.dto.PartAnalysisDataDTO;
import com.projectwz.partsforecast.dto.PartBasicInfoDTO;
import com.projectwz.partsforecast.dto.PartForecastDetailDTO;
import com.projectwz.partsforecast.dto.HistoricalAnalysisDTO;
import com.projectwz.partsforecast.service.PartForecastAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartForecastController {

    @Autowired
    private PartForecastAnalysisService partForecastAnalysisService;


    // API for 配件预测分析 - list parts (for table in Fig 5-9 [cite: 12])
//    @GetMapping
//    public ResponseEntity<List<PartBasicInfoDTO>> getAllPartsBasicInfo(@RequestParam(required = false) Integer agentId) {
//        List<PartBasicInfoDTO> parts = partForecastAnalysisService.getAllPartsBasicInfo(agentId);
//        return ResponseEntity.ok(parts);
//    }

    // API for 配件预测分析 - get single part detail with forecast (Fig 5-9, "预测" button action [cite: 12])
    // Corresponds to SaleDetail, ProfitDetail, StoreDetail concepts for fetching part data [cite: 11]
// 在 PartForecastController.java 中

    @GetMapping("/{partIdOrCode}/forecast-detail")
    public ResponseEntity<PartForecastDetailDTO> getPartForecastDetail(
            @PathVariable String partIdOrCode,
            @RequestParam(defaultValue = "12") int forecastPoints) { // 前端请求的参数名是 forecastPoints
        PartForecastDetailDTO detail = partForecastAnalysisService.getPartForecastDetail(partIdOrCode, forecastPoints);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }



    // API for 预测结果分析 (Fig 5-12 [cite: 15])
    // Corresponds to BuySelect and ForecastList from sequence diagram [cite: 14]
//    @GetMapping("/{partId}/historical-analysis")
//    public ResponseEntity<HistoricalAnalysisDTO> getHistoricalAnalysis(
//            @PathVariable Integer partId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
//        HistoricalAnalysisDTO analysis = partForecastAnalysisService.getHistoricalAnalysis(partId, startDate, endDate);
//        return ResponseEntity.ok(analysis);
//    }

    // 在 PartForecastController.java 中
    @GetMapping
    public ResponseEntity<List<PartBasicInfoDTO>> getPartsList(
            @RequestParam(required = false) Integer agentId,
            @RequestParam(required = false) String category,    // 一级分类 (partType)
            @RequestParam(required = false) String subCategory, // 二级分类 (partSpec)
            @RequestParam(required = false) String name) {
        List<PartBasicInfoDTO> parts = partForecastAnalysisService.getPartsList(agentId, category, subCategory, name);
        return ResponseEntity.ok(parts);
    }

    // TODO: Add API for comparing multiple parts (Fig 5-10 [cite: 13])
    // @PostMapping("/compare-forecasts")
    // public ResponseEntity<List<PartComparisonResponseDTO>> comparePartForecasts(@RequestBody PartComparisonRequestDTO request) { ... }


    @GetMapping("/analysis/{partIdentifier}")
    public ResponseEntity<PartAnalysisDataDTO> getPartAnalysis(
            @PathVariable String partIdentifier,
            @RequestParam(defaultValue = "6") int historyMonths, // 默认获取最近6个月历史数据
            @RequestParam(defaultValue = "12") int futureForecastPeriods // 默认获取未来12个周期的预测（如果需求趋势图需要）
    ) {
        PartAnalysisDataDTO analysisData = partForecastAnalysisService.getPartAnalysisData(partIdentifier, historyMonths, futureForecastPeriods);
        if (analysisData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analysisData);
    }

}