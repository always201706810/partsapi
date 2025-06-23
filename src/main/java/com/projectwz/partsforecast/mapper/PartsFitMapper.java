package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.PartsFit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface PartsFitMapper {
    List<Map<String, Object>> findTopProfitableParts(@Param("agentId") Integer agentId, @Param("limit") int limit);

    List<PartsFit> findByPartsInfoId(@Param("partsId") Integer partsId, @Param("limit") int limit);

    List<PartsFit> findByPartsInfoIdAndFitDateBetweenOrderByFitDateAsc(
            @Param("partsId") Integer partsId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}