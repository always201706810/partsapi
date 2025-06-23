package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.PartsPurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PartsPurchaseMapper {
    List<PartsPurchase> findByPartsInfoIdOrderByBuyDateDesc(@Param("partsId") Integer partsId, @Param("limit") int limit);
}