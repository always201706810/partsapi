package com.projectwz.partsforecast.typehandler;

import com.projectwz.partsforecast.entity.ModelStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 这是用于 MyBatis 的自定义类型处理器。
 * 它负责将 Java 中的 ModelStatus 枚举类型 与 数据库中的 INT 类型进行转换。
 */
@SuppressWarnings("rawtypes")
public class ModelStatusTypeHandler implements TypeHandler<ModelStatus> {

    @Override
    public void setParameter(PreparedStatement ps, int i, ModelStatus parameter, JdbcType jdbcType) throws SQLException {
        // 在向数据库写入时，将 ModelStatus 枚举转换为其对应的整数码
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public ModelStatus getResult(ResultSet rs, String columnName) throws SQLException {
        // 在从数据库读取时，根据列名获取整数码，并转换为 ModelStatus 枚举
        int code = rs.getInt(columnName);
        return ModelStatus.fromCode(code);
    }

    @Override
    public ModelStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        // 在从数据库读取时，根据列索引获取整数码，并转换为 ModelStatus 枚举
        int code = rs.getInt(columnIndex);
        return ModelStatus.fromCode(code);
    }

    @Override
    public ModelStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 在从数据库读取存储过程结果时，获取整数码，并转换为 ModelStatus 枚举
        int code = cs.getInt(columnIndex);
        return ModelStatus.fromCode(code);
    }
}