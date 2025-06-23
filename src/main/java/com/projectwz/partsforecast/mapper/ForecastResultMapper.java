package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.ForecastResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ForecastResultMapper {
    List<ForecastResult> findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(
            @Param("partsId") Integer partsId,
            @Param("startTime") LocalDateTime startTime,
            @Param("limit") int limit);

    ForecastResult findTopByPartsInfoIdOrderByForecastTimeDesc(Integer partsId);
}