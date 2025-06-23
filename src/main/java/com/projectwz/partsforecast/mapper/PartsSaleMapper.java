package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.PartsSale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface PartsSaleMapper {
    List<Map<String, Object>> findTopSellingParts(@Param("agentId") Integer agentId, @Param("limit") int limit);

    List<PartsSale> findByPartsInfoIdOrderBySaleDateDesc(@Param("partsId") Integer partsId, @Param("limit") int limit);

    List<PartsSale> findByPartsInfoIdAndSaleDateBetweenOrderBySaleDateAsc(
            @Param("partsId") Integer partsId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}