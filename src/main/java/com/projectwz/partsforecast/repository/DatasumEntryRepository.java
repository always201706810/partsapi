// com/projectwz/partsforecast/repository/DatasumEntryRepository.java
package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.DatasumEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface DatasumEntryRepository extends JpaRepository<DatasumEntry, Integer> {
    List<DatasumEntry> findByAgentInfoId(Integer agentId);
    List<DatasumEntry> findByPartsIdString(String partsIdString); // parts_id is VARCHAR in datasum
    List<DatasumEntry> findByPartsNameAndTimeIndexBetween(String partsName, LocalDateTime start, LocalDateTime end);

    List<DatasumEntry> findByPartsIdStringAndTimeIndexBetweenOrderByTimeIndexAsc(String partsIdString, LocalDateTime startTime, LocalDateTime endTime);

}