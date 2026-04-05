package com.haidong.tuanwei.system.service;

import com.haidong.tuanwei.system.dto.RegionForm;
import com.haidong.tuanwei.system.entity.Region;
import java.util.List;

public interface RegionService {

    List<Region> getRegionTree();

    List<Region> search(Integer regionLevel, Long parentId, String keyword, int page, int pageSize);

    long count(Integer regionLevel, Long parentId, String keyword);

    List<Region> getFlatRegions();

    List<Region> getRegionsByLevel(Integer regionLevel);

    Region getById(Long id);

    void create(RegionForm request);

    void update(Long id, RegionForm request);

    void delete(Long id);
}
