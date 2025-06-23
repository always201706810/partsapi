package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.PartsStore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PartsStoreMapper {
    PartsStore findTopByPartsInfoIdOrderByStoreTimeDesc(Integer partsId);

    List<Map<String, Object>> findTopTurnoverParts(@Param("agentId") Integer agentId, @Param("limit") int limit);
}