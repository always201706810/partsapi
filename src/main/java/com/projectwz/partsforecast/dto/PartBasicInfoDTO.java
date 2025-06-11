package com.projectwz.partsforecast.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PartBasicInfoDTO {
    private Integer partId;         // 后端内部ID，前端可能不需要直接展示，但可用于请求详情
    private String code;            // 对应前端: code (来自 PartsInfo.partCode)
    private String name;            // 对应前端: name (来自 PartsInfo.partName)
    private String category;        // 对应前端: category (来自 PartsInfo.partType)
    private String subCategory;     // 对应前端: subCategory (来自 PartsInfo.partSpec)
    private BigDecimal stock;         // 对应前端: stock
    private BigDecimal purchasePrice; // 对应前端: purchasePrice
    private BigDecimal salePrice;     // 对应前端: salePrice
    private BigDecimal profit;        // 对应前端: profit
    private String turnoverRate;    // 对应前端: turnoverRate (例如 "10%")
    private String profitRate;      // 对应前端: profitRate (例如 "28.6%")
    private BigDecimal revenue;       // 对应前端: revenue

    // 对应前端表格展开行的 week1-week5
    private BigDecimal week1;
    private BigDecimal week2;
    private BigDecimal week3;
    private BigDecimal week4;
    private BigDecimal week5;
}