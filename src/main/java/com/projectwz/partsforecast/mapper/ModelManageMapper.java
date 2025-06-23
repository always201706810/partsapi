package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.ModelManage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface ModelManageMapper {
    // 对应复杂的动态查询，在 XML 中实现
    List<ModelManage> searchModels(Map<String, Object> params);

    // 对应 findById，在 XML 中实现以包含关联对象
    Optional<ModelManage> findById(Integer modelId);

    // 对应 save（新增），在 XML 中实现
    void insert(ModelManage model);

    // 对应 save（更新），在 XML 中实现
    void update(ModelManage model);
}