// com/projectwz/partsforecast/repository/PartsFitRepository.java
package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.PartsFit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartsFitRepository extends JpaRepository<PartsFit, Integer> {
//    @Query("SELECT pf.partsInfo.partName, SUM(pf.fitPartPrice) as totalProfit " +
//            "FROM PartsFit pf " +
//            "WHERE (:agentId IS NULL OR pf.agentInfo.id = :agentId) " + // Allow agentId to be optional
//            "GROUP BY pf.partsInfo.id, pf.partsInfo.partName " +
//            "ORDER BY totalProfit DESC")
//    List<Object[]> findTopProfitableParts(@Param("agentId") Integer agentId, Pageable pageable);
        @Query("SELECT pf.partsInfo.partName, SUM(pf.fitPartPrice) as totalProfit, pf.partsInfo.id as partId " +
                "FROM PartsFit pf " +
                "WHERE (:agentId IS NULL OR pf.agentInfo.id = :agentId) " + // Allow agentId to be optional
                "GROUP BY pf.partsInfo.id, pf.partsInfo.partName " +
                "ORDER BY totalProfit DESC")
        List<Object[]> findTopProfitableParts(@Param("agentId") Integer agentId, Pageable pageable);
        List<PartsFit> findByPartsInfoId(Integer partsId, Pageable pageable);

        List<PartsFit> findByPartsInfoIdAndFitDateBetweenOrderByFitDateAsc(Integer partsId, LocalDateTime startDate, LocalDateTime endDate);
}