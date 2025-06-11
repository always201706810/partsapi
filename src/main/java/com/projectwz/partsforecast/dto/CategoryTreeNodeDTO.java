package com.projectwz.partsforecast.dto;
// ... (代码同上一条回复)
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

// com/projectwz/partsforecast/dto/CategoryTreeNodeDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeNodeDTO {
    private String label;
    private List<CategoryTreeNodeDTO> children;
    private boolean isLeaf;
    private String categoryValue; // 对于父节点是 partType，对于子节点是 partSpec
    private String parentCategoryValue; // 新增：仅子节点有值，表示其父节点的 categoryValue (即 partType)
    private String type; // "category" 或 "subCategory"
}