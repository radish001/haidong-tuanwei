package com.haidong.tuanwei.enterprise.service;

import com.haidong.tuanwei.enterprise.dto.EnterpriseFormRequest;
import com.haidong.tuanwei.enterprise.dto.EnterpriseSearchRequest;
import com.haidong.tuanwei.enterprise.entity.EnterpriseInfo;
import java.util.List;

public interface EnterpriseService {

    List<EnterpriseInfo> search(EnterpriseSearchRequest query);

    long count(EnterpriseSearchRequest query);

    List<EnterpriseInfo> getAllEnabled();

    EnterpriseInfo getById(Long id);

    void create(EnterpriseFormRequest request, Long operatorId);

    void update(Long id, EnterpriseFormRequest request, Long operatorId);

    void delete(Long id, Long operatorId);

    int deleteBatch(List<Long> ids, Long operatorId);
}
