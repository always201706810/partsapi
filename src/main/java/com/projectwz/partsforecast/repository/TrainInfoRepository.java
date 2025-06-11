// com/projectwz/partsforecast/repository/TrainInfoRepository.java
package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.TrainInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainInfoRepository extends JpaRepository<TrainInfo, Integer> {
    List<TrainInfo> findByModelManageId(Integer modelId);
    List<TrainInfo> findByPartsInfoId(Integer partsId);
    // 如果 TrainInfo 可以通过 PartsInfo 间接关联到 AgentInfo
    List<TrainInfo> findByPartsInfo_AgentInfo_Id(Integer agentId);
    // 新增：根据模型ID查找最新的一条训练信息
    Optional<TrainInfo> findTopByModelManageIdOrderByIdDesc(Integer modelId);
}