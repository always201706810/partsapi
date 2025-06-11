package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.PartsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <--- 确保这个存在
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
// ...其他导入...

@Repository
public interface PartsInfoRepository extends JpaRepository<PartsInfo, Integer>, JpaSpecificationExecutor<PartsInfo> { // <--- 继承 JpaSpecificationExecutor
    // ... 你自定义的其他查询方法 ...
    List<PartsInfo> findByAgentInfoId(Integer agentId);
    List<PartsInfo> findByPartType(String partType);
    List<PartsInfo> findByPartNameContaining(String name);
    Optional<PartsInfo> findByPartCode(String partCode);
    long countByAgentInfoId(Integer agentId);
}