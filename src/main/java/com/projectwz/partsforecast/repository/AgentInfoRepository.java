// com/projectwz/partsforecast/repository/AgentInfoRepository.java
package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.AgentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentInfoRepository extends JpaRepository<AgentInfo, Integer> {
}