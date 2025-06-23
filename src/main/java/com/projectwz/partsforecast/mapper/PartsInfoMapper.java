package com.projectwz.partsforecast.mapper;

import com.projectwz.partsforecast.entity.PartsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface PartsInfoMapper {
    long countByAgentInfoId(Integer agentId);

    @Select("SELECT COUNT(*) FROM parts_info")
    long countAll();

    List<PartsInfo> findAllWithFilter(Map<String, Object> params);

    @Select("SELECT * FROM parts_info WHERE id = #{id}")
    Optional<PartsInfo> findById(Integer id);

    @Select("SELECT * FROM parts_info WHERE part_code = #{partCode}")
    Optional<PartsInfo> findByPartCode(String partCode);

    @Select("SELECT * FROM parts_info WHERE part_name LIKE CONCAT('%', #{name}, '%')")
    List<PartsInfo> findByPartNameContaining(String name);

    @Select("SELECT * FROM parts_info")
    List<PartsInfo> findAll();
}