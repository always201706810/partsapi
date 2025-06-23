package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.AgentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AgentInfoMapper {
    @Select("SELECT * FROM agent_info")
    List<AgentInfo> findAll();

    @Select("SELECT * FROM agent_info WHERE id = #{id}")
    Optional<AgentInfo> findById(Integer id);
}