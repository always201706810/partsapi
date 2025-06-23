package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.TrainInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TrainInfoMapper {
    List<TrainInfo> findByAgentId(Integer agentId);
}