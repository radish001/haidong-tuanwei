package com.haidong.tuanwei.policy.dao;

import com.haidong.tuanwei.policy.dto.PolicySearchRequest;
import com.haidong.tuanwei.policy.entity.PolicyArticle;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PolicyArticleDao {

    List<PolicyArticle> search(@Param("query") PolicySearchRequest query);

    long count(@Param("query") PolicySearchRequest query);

    PolicyArticle findById(@Param("id") Long id);

    int insert(PolicyArticle article);

    int update(PolicyArticle article);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updateBy") Long updateBy);

    int softDelete(@Param("id") Long id, @Param("updateBy") Long updateBy);

    int softDeleteBatch(@Param("ids") List<Long> ids, @Param("updateBy") Long updateBy);
}
