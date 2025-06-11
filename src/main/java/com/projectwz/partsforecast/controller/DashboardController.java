// com/projectwz/partsforecast/controller/DashboardController.java
package com.projectwz.partsforecast.controller;

import com.projectwz.partsforecast.dto.DashboardOverviewDTO;
import com.projectwz.partsforecast.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private DashboardService dashboardService;

    // API for 总体看板
    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDTO> getDashboardOverview(@RequestParam(required = false) Integer agentId) {
        DashboardOverviewDTO overview = dashboardService.getDashboardOverview(agentId);
        return ResponseEntity.ok(overview);
    }

    // API for specific part's next forecast (ForecastResult from sequence diagram)
    @GetMapping("/parts/{partId}/next-forecast")
    public ResponseEntity<Map<String, BigDecimal>> getPartNextForecast(@PathVariable Integer partId) {
        BigDecimal forecastValue = dashboardService.getPartNextForecast(partId);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("nextForecastValue", forecastValue);
        return ResponseEntity.ok(response);
    }
}