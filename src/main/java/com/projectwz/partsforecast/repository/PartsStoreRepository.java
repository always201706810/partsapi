// com/projectwz/partsforecast/repository/PartsStoreRepository.java
package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.PartsStore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartsStoreRepository extends JpaRepository<PartsStore, Integer> {
    PartsStore findTopByPartsInfoIdOrderByStoreTimeDesc(Integer partsId);

//    @Query("SELECT ps.partsInfo.partName, ps.storePartRate as turnoverRate " +
//            "FROM PartsStore ps " +
//            "WHERE ps.id IN (SELECT MAX(sub.id) FROM PartsStore sub WHERE (:agentId IS NULL OR sub.agentInfo.id = :agentId) GROUP BY sub.partsInfo.id) " + // Get latest stock entry per part
//            "ORDER BY turnoverRate DESC")
//    List<Object[]> findTopTurnoverParts(@Param("agentId") Integer agentId, Pageable pageable);
@Query("SELECT ps.partsInfo.partName, ps.storePartRate as turnoverRate , ps.partsInfo.id as partId " +
        "FROM PartsStore ps " +
        "WHERE ps.id IN (SELECT MAX(sub.id) FROM PartsStore sub WHERE (:agentId IS NULL OR sub.agentInfo.id = :agentId) GROUP BY sub.partsInfo.id) " + // Get latest stock entry per part
        "ORDER BY turnoverRate DESC")
List<Object[]> findTopTurnoverParts(@Param("agentId") Integer agentId, Pageable pageable);
}