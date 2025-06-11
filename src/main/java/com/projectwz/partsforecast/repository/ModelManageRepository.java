package com.projectwz.partsforecast.repository;

import com.projectwz.partsforecast.entity.ModelManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 导入 JpaSpecificationExecutor
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelManageRepository extends JpaRepository<ModelManage, Integer>, JpaSpecificationExecutor<ModelManage> { // 继承 JpaSpecificationExecutor
    // 你可以保留已有的方法
    List<ModelManage> findByIsDeleted(Integer isDeleted);
    List<ModelManage> findByModelNameContainingAndIsDeleted(String modelName, Integer isDeleted);
}