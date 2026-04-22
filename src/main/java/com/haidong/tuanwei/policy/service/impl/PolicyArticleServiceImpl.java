package com.haidong.tuanwei.policy.service.impl;

import com.haidong.tuanwei.policy.dao.PolicyArticleDao;
import com.haidong.tuanwei.policy.dto.PolicyFormRequest;
import com.haidong.tuanwei.policy.dto.PolicySearchRequest;
import com.haidong.tuanwei.policy.entity.PolicyArticle;
import com.haidong.tuanwei.policy.service.PolicyArticleService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyArticleServiceImpl implements PolicyArticleService {

    private final PolicyArticleDao policyArticleDao;

    @Override
    public List<PolicyArticle> search(PolicySearchRequest query) {
        return policyArticleDao.search(query);
    }

    @Override
    public long count(PolicySearchRequest query) {
        return policyArticleDao.count(query);
    }

    @Override
    public PolicyArticle getById(Long id) {
        return policyArticleDao.findById(id);
    }

    @Override
    public void create(PolicyFormRequest request, Long operatorId) {
        PolicyArticle article = toEntity(request);
        article.setStatus(0);
        policyArticleDao.insert(article);
    }

    @Override
    public void update(Long id, PolicyFormRequest request, Long operatorId) {
        PolicyArticle article = toEntity(request);
        article.setId(id);
        policyArticleDao.update(article);
    }

    @Override
    public void updateStatus(Long id, Integer status, Long operatorId) {
        policyArticleDao.updateStatus(id, status, operatorId);
    }

    @Override
    public void delete(Long id, Long operatorId) {
        policyArticleDao.softDelete(id, operatorId);
    }

    @Override
    public int deleteBatch(List<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return policyArticleDao.softDeleteBatch(ids, operatorId);
    }

    private PolicyArticle toEntity(PolicyFormRequest request) {
        PolicyArticle article = new PolicyArticle();
        article.setTitle(request.getTitle());
        article.setIssuingOrganization(request.getIssuingOrganization());
        article.setPolicySource(request.getPolicySource());
        article.setSummary(request.getSummary());
        article.setSortOrder(request.getSortOrder());
        article.setContentHtml(request.getContentHtml());
        article.setPublishTime(LocalDateTime.now());
        return article;
    }
}
