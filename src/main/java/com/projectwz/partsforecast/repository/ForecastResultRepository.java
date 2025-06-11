// com/projectwz/partsforecast/repository/ForecastResultRepository.java
package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.ForecastResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ForecastResultRepository extends JpaRepository<ForecastResult, Integer> {
    List<ForecastResult> findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(Integer partsId, LocalDateTime startTime);
    List<ForecastResult> findByPartsInfoIdAndForecastTimeGreaterThanEqualOrderByForecastTimeAsc(Integer partsId, LocalDateTime startTime, Pageable pageable);
    ForecastResult findTopByPartsInfoIdOrderByForecastTimeDesc(Integer partsId);
    List<ForecastResult> findByPartsInfoIdAndForecastTimeBetweenOrderByForecastTimeAsc(Integer partsId, LocalDateTime startDate, LocalDateTime endDate);
}