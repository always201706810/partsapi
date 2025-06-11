// com/projectwz/partsforecast/repository/PartsSaleRepository.java
package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.PartsSale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartsSaleRepository extends JpaRepository<PartsSale, Integer> {
//    @Query("SELECT ps.partsInfo.partName, SUM(ps.salePartNum) as totalQuantity " +
//            "FROM PartsSale ps " +
//            "WHERE (:agentId IS NULL OR ps.agentInfo.id = :agentId) " + // Allow agentId to be optional
//            "GROUP BY ps.partsInfo.id, ps.partsInfo.partName " +
//            "ORDER BY totalQuantity DESC")
//    List<Object[]> findTopSellingParts(@Param("agentId") Integer agentId, Pageable pageable);
// com/projectwz/partsforecast/repository/PartsSaleRepository.java
        @Query("SELECT ps.partsInfo.partName, SUM(ps.salePartNum) as totalQuantity, ps.partsInfo.id as partId " + // 添加 ps.partsInfo.id
                "FROM PartsSale ps " +
                "WHERE (:agentId IS NULL OR ps.agentInfo.id = :agentId) " +
                "GROUP BY ps.partsInfo.id, ps.partsInfo.partName " + // 确保分组正确
                "ORDER BY totalQuantity DESC")
        List<Object[]> findTopSellingParts(@Param("agentId") Integer agentId, Pageable pageable);
        List<PartsSale> findByPartsInfoIdOrderBySaleDateDesc(Integer partsId, Pageable pageable);

        List<PartsSale> findByPartsInfoIdAndSaleDateBetweenOrderBySaleDateAsc(Integer partsId, LocalDateTime startDate, LocalDateTime endDate);
}