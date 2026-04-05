package com.haidong.tuanwei.enterprise.dao;

import com.haidong.tuanwei.enterprise.dto.EnterpriseSearchRequest;
import com.haidong.tuanwei.enterprise.entity.EnterpriseInfo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EnterpriseDao {

    List<EnterpriseInfo> search(@Param("query") EnterpriseSearchRequest query);

    long count(@Param("query") EnterpriseSearchRequest query);

    List<EnterpriseInfo> findAllEnabled();

    EnterpriseInfo findById(@Param("id") Long id);

    int insert(EnterpriseInfo enterpriseInfo);

    int update(EnterpriseInfo enterpriseInfo);

    int softDelete(@Param("id") Long id, @Param("updateBy") Long updateBy);

    int softDeleteBatch(@Param("ids") List<Long> ids, @Param("updateBy") Long updateBy);
}
