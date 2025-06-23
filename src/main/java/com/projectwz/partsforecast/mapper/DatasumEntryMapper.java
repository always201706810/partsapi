package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.DatasumEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DatasumEntryMapper {
    List<DatasumEntry> findByPartsIdStringAndTimeIndexBetweenOrderByTimeIndexAsc(
            @Param("partsIdString") String partsIdString,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}