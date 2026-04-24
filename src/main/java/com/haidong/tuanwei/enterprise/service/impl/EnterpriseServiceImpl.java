package com.haidong.tuanwei.enterprise.service.impl;

import com.haidong.tuanwei.system.dao.DictionaryDao;
import com.haidong.tuanwei.system.entity.DictItem;
import com.haidong.tuanwei.system.service.MasterDataService;
import com.haidong.tuanwei.system.support.RegionSelectionSupport;
import com.haidong.tuanwei.enterprise.dao.EnterpriseDao;
import com.haidong.tuanwei.enterprise.dto.EnterpriseFormRequest;
import com.haidong.tuanwei.enterprise.dto.EnterpriseSearchRequest;
import com.haidong.tuanwei.enterprise.entity.EnterpriseInfo;
import com.haidong.tuanwei.enterprise.service.EnterpriseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnterpriseServiceImpl implements EnterpriseService {

    private final EnterpriseDao enterpriseDao;
    private final DictionaryDao dictionaryDao;
    private final RegionSelectionSupport regionSelectionSupport;
    private final MasterDataService masterDataService;

    @Override
    public List<EnterpriseInfo> search(EnterpriseSearchRequest query) {
        return enterpriseDao.search(query);
    }

    @Override
    public long count(EnterpriseSearchRequest query) {
        return enterpriseDao.count(query);
    }

    @Override
    public List<EnterpriseInfo> getAllEnabled() {
        return enterpriseDao.findAllEnabled();
    }

    @Override
    public EnterpriseInfo getById(Long id) {
        return enterpriseDao.findById(id);
    }

    @Override
    public void create(EnterpriseFormRequest request, Long operatorId) {
        EnterpriseInfo enterprise = toEntity(request);
        enterprise.setStatus(1);
        enterpriseDao.insert(enterprise);
    }

    @Override
    public void update(Long id, EnterpriseFormRequest request, Long operatorId) {
        EnterpriseInfo enterprise = toEntity(request);
        enterprise.setId(id);
        enterprise.setStatus(1);
        enterprise.setSortOrder(resolveSortOrderForUpdate(id, request.getSortOrder()));
        enterpriseDao.update(enterprise);
    }

    @Override
    public void delete(Long id, Long operatorId) {
        enterpriseDao.softDelete(id, operatorId);
    }

    @Override
    public int deleteBatch(List<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return enterpriseDao.softDeleteBatch(ids, operatorId);
    }

    private EnterpriseInfo toEntity(EnterpriseFormRequest request) {
        EnterpriseInfo enterprise = new EnterpriseInfo();
        enterprise.setEnterpriseName(request.getEnterpriseName());
        enterprise.setIndustry(validateDictValue("enterprise_industry", request.getIndustry(), "企业行业"));
        enterprise.setEnterpriseNature(validateDictValue("enterprise_nature", request.getEnterpriseNature(), "企业性质"));
        enterprise.setEnterpriseScale(validateDictValue("enterprise_scale", request.getEnterpriseScale(), "企业规模"));
        RegionSelectionSupport.RegionSelection regionSelection = regionSelectionSupport.normalize(
                request.getRegionProvinceCode(), request.getRegionCityCode(), request.getRegionCountyCode(), "所在地区");
        enterprise.setRegionProvinceCode(regionSelection.getProvinceCode());
        enterprise.setRegionCityCode(regionSelection.getCityCode());
        enterprise.setRegionCountyCode(regionSelection.getCountyCode());
        enterprise.setUnifiedSocialCreditCode(request.getUnifiedSocialCreditCode());
        enterprise.setBusinessLicensePath(request.getBusinessLicensePath());
        enterprise.setAddress(request.getAddress());
        enterprise.setContactPerson(request.getContactPerson());
        enterprise.setContactPhone(request.getContactPhone());
        enterprise.setDescription(request.getDescription());
        enterprise.setSortOrder(request.getSortOrder());
        return enterprise;
    }

    private String validateDictValue(String dictType, String value, String label) {
        if (value == null || value.isBlank()) {
            return null;
        }
        List<DictItem> items = dictionaryDao.findByType(dictType);
        return items.stream()
                .map(DictItem::getDictValue)
                .filter(itemValue -> itemValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(label + "不在基础数据范围内"));
    }

    private Integer resolveSortOrderForUpdate(Long id, Integer requestedSortOrder) {
        if (masterDataService.isSortFieldVisible() || requestedSortOrder != null) {
            return requestedSortOrder;
        }
        EnterpriseInfo existing = enterpriseDao.findById(id);
        return existing == null ? null : existing.getSortOrder();
    }
}
