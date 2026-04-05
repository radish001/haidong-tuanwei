package com.haidong.tuanwei.policy.service;

import com.haidong.tuanwei.policy.dto.PolicyFormRequest;
import com.haidong.tuanwei.policy.dto.PolicySearchRequest;
import com.haidong.tuanwei.policy.entity.PolicyArticle;
import java.util.List;

public interface PolicyArticleService {

    List<PolicyArticle> search(PolicySearchRequest query);

    long count(PolicySearchRequest query);

    PolicyArticle getById(Long id);

    void create(PolicyFormRequest request, Long operatorId);

    void update(Long id, PolicyFormRequest request, Long operatorId);

    void updateStatus(Long id, Integer status, Long operatorId);

    void delete(Long id, Long operatorId);

    int deleteBatch(List<Long> ids, Long operatorId);
}
