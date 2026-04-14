package com.haidong.tuanwei.system.dao;

import com.haidong.tuanwei.system.entity.Region;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RegionDao {

    List<Region> findAll();

    List<Region> search(@Param("regionLevel") Integer regionLevel,
            @Param("parentId") Long parentId,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long count(@Param("regionLevel") Integer regionLevel,
            @Param("parentId") Long parentId,
            @Param("keyword") String keyword);

    Region findById(@Param("id") Long id);

    int insert(Region region);

    int update(Region region);

    int delete(@Param("id") Long id);

    Region findByCode(String regionCode);

    long countChildren(@Param("parentId") Long parentId);

    long countUsage(@Param("regionCode") String regionCode);

    List<Region> findByLevel(@Param("regionLevel") Integer regionLevel);
}
