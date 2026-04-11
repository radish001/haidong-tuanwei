package com.haidong.tuanwei.system.service.impl;

import com.haidong.tuanwei.system.dao.RegionDao;
import com.haidong.tuanwei.system.dto.RegionForm;
import com.haidong.tuanwei.system.entity.Region;
import com.haidong.tuanwei.system.service.RegionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionDao regionDao;

    @Cacheable("regionTree")
    @Override
    public List<Region> getRegionTree() {
        List<Region> flatRegions = getFlatRegions();
        Map<Long, Region> regionMap = new HashMap<>();
        List<Region> roots = new ArrayList<>();

        for (Region region : flatRegions) {
            region.getChildren().clear();
            regionMap.put(region.getId(), region);
        }

        for (Region region : flatRegions) {
            if (region.getParentId() == null || region.getParentId() == 0) {
                roots.add(region);
                continue;
            }
            Region parent = regionMap.get(region.getParentId());
            if (parent != null) {
                parent.getChildren().add(region);
            }
        }
        return roots;
    }

    @Override
    public List<Region> search(Integer regionLevel, Long parentId, String keyword, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(1, Math.min(pageSize, 100));
        return regionDao.search(regionLevel, parentId, keyword, (safePage - 1) * safePageSize, safePageSize);
    }

    @Override
    public long count(Integer regionLevel, Long parentId, String keyword) {
        return regionDao.count(regionLevel, parentId, keyword);
    }

    @CacheEvict(value = "regionTree", allEntries = true)
    @Override
    public void create(RegionForm request) {
        validateHierarchy(request.getRegionLevel(), request.getParentId());
        Region region = new Region();
        region.setParentId(request.getParentId());
        region.setRegionCode(request.getRegionCode());
        region.setRegionName(request.getRegionName());
        region.setRegionLevel(request.getRegionLevel());
        region.setSortNo(0);
        regionDao.insert(region);
        log.info("Region created: id={}, level={}, code={}, parentId={}",
                region.getId(), region.getRegionLevel(), region.getRegionCode(), region.getParentId());
    }

    @Override
    public List<Region> getFlatRegions() {
        List<Region> flatRegions = regionDao.findAll();
        Map<Long, Region> sourceMap = new HashMap<>();
        for (Region region : flatRegions) {
            sourceMap.put(region.getId(), region);
        }
        for (Region region : flatRegions) {
            region.setFullName(buildFullName(region, sourceMap));
        }
        return flatRegions;
    }

    @Override
    public List<Region> getRegionsByLevel(Integer regionLevel) {
        return regionDao.findByLevel(regionLevel);
    }

    @Override
    public Region getById(Long id) {
        return regionDao.findById(id);
    }

    @CacheEvict(value = "regionTree", allEntries = true)
    @Override
    public void update(Long id, RegionForm request) {
        Region existing = requireExisting(id);
        validateHierarchy(request.getRegionLevel(), request.getParentId());
        if (regionDao.countChildren(id) > 0 && !request.getRegionLevel().equals(existing.getRegionLevel())) {
            throw new IllegalStateException("存在下级区域时不允许修改区域层级");
        }
        existing.setParentId(request.getParentId());
        existing.setRegionCode(request.getRegionCode());
        existing.setRegionName(request.getRegionName());
        existing.setRegionLevel(request.getRegionLevel());
        regionDao.update(existing);
        log.info("Region updated: id={}, level={}, code={}, parentId={}",
                id, existing.getRegionLevel(), existing.getRegionCode(), existing.getParentId());
    }

    @CacheEvict(value = "regionTree", allEntries = true)
    @Override
    public void delete(Long id) {
        Region existing = requireExisting(id);
        if (regionDao.countChildren(id) > 0) {
            throw new IllegalStateException("该区域存在下级区域关联，无法删除");
        }
        if (regionDao.countUsage(existing.getRegionCode()) > 0) {
            throw new IllegalStateException("该区域已被业务数据使用，无法删除");
        }
        regionDao.softDelete(id);
        log.info("Region deleted: id={}, level={}, code={}", id, existing.getRegionLevel(), existing.getRegionCode());
    }

    private String buildFullName(Region region, Map<Long, Region> sourceMap) {
        if (region.getParentId() == null || region.getParentId() == 0) {
            return region.getRegionName();
        }
        Region parent = sourceMap.get(region.getParentId());
        if (parent == null) {
            return region.getRegionName();
        }
        return buildFullName(parent, sourceMap) + "-" + region.getRegionName();
    }

    private void validateHierarchy(Integer regionLevel, Long parentId) {
        if (regionLevel == null || regionLevel < 1 || regionLevel > 3) {
            throw new IllegalStateException("区域层级仅支持省、市、区（县）三级");
        }
        if (regionLevel == 1) {
            return;
        }
        if (parentId == null || parentId <= 0) {
            throw new IllegalStateException("当前层级必须选择上级区域");
        }
        Region parent = regionDao.findById(parentId);
        if (parent == null) {
            throw new IllegalStateException("上级区域不存在");
        }
        if (parent.getRegionLevel() != regionLevel - 1) {
            throw new IllegalStateException("上级区域层级不合法");
        }
    }

    private Region requireExisting(Long id) {
        Region region = regionDao.findById(id);
        if (region == null) {
            throw new IllegalStateException("区域不存在或已删除");
        }
        return region;
    }
}
