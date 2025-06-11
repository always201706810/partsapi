package com.projectwz.partsforecast.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartsStatsDTO {
    private long totalParts;  // 配件总数
    private long headParts;   // 头部配件数 (parts_len >= 200)
    private long tailParts;   // 尾部配件数 (parts_len < 200)
}