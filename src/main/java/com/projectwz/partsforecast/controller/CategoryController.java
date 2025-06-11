package com.projectwz.partsforecast.controller;

import com.projectwz.partsforecast.dto.CategoryTreeNodeDTO;
import com.projectwz.partsforecast.service.PartForecastAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/parts/categories") // 确保与前端 API 调用路径一致
public class CategoryController {

    @Autowired
    private PartForecastAnalysisService partForecastAnalysisService;

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeNodeDTO>> getCategoryTree() {
        List<CategoryTreeNodeDTO> treeData = partForecastAnalysisService.getCategoryTree();
        return ResponseEntity.ok(treeData);
    }
}