// com/projectwz/partsforecast/repository/PartsPurchaseRepository.java
package com.projectwz.partsforecast.repository;
import org.springframework.data.domain.Pageable;
import com.projectwz.partsforecast.entity.PartsPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartsPurchaseRepository extends JpaRepository<PartsPurchase, Integer> {
    List<PartsPurchase> findByPartsInfoIdAndBuyDateBetweenOrderByBuyDateAsc(Integer partsId, LocalDateTime startDate, LocalDateTime endDate);
    List<PartsPurchase> findByPartsInfoIdOrderByBuyDateDesc(Integer partsId, Pageable pageable);

}