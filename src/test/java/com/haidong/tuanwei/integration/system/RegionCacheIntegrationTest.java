package com.haidong.tuanwei.integration.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.haidong.tuanwei.integration.IntegrationTestBase;
import com.haidong.tuanwei.system.entity.Region;
import com.haidong.tuanwei.system.service.RegionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;

/**
 * 区域缓存集成测试
 * 验证 @Cacheable 和 @CacheEvict 行为
 */
class RegionCacheIntegrationTest extends IntegrationTestBase {

    @Autowired
    private RegionService regionService;

    @Autowired
    private CacheManager cacheManager;

    private static final String CACHE_NAME = "regionTree";

    /**
     * 获取缓存键：无参数方法使用 SimpleKey.EMPTY
     */
    private Object getCacheKey() {
        return SimpleKey.EMPTY;
    }

    @Test
    void regionTreeShouldBeCachedAfterFirstCall() {
        // 首次调用 - 会查询数据库并建立缓存
        List<Region> firstResult = regionService.getRegionTree();
        assertThat(firstResult).isNotEmpty();

        // 验证缓存已建立
        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache).isNotNull();
        // 无参数方法的缓存键是 SimpleKey.EMPTY
        assertThat(cache.get(getCacheKey())).isNotNull();
    }

    @Test
    void regionCacheShouldBeEvictedAfterCreate() throws Exception {
        // 建立缓存
        regionService.getRegionTree();
        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(getCacheKey())).isNotNull();

        // 通过 API 创建新区域
        String regionCode = "999" + System.currentTimeMillis();
        mockMvc.perform(post("/system/regions")
                        .session(adminSession)
                        .param("regionCode", regionCode)
                        .param("regionName", "测试缓存失效区域")
                        .param("regionLevel", "1")
                        .param("parentId", "0"))
                .andExpect(status().is3xxRedirection());

        // 验证缓存已被清空（allEntries = true 会清空所有条目）
        assertThat(cache.get(getCacheKey())).isNull();

        // 再次调用，应该重新查库并重建缓存
        List<Region> result = regionService.getRegionTree();
        assertThat(result).isNotEmpty();
        // 包含刚创建的省级区域（在根列表中）
        assertThat(result.stream())
                .anyMatch(r -> regionCode.equals(r.getRegionCode()));
    }

    @Test
    void regionCacheShouldBeEvictedAfterUpdate() throws Exception {
        // 先创建一个测试区域
        String regionCode = "UPD" + System.currentTimeMillis();
        mockMvc.perform(post("/system/regions")
                        .session(adminSession)
                        .param("regionCode", regionCode)
                        .param("regionName", "原区域名")
                        .param("regionLevel", "1")
                        .param("parentId", "0"))
                .andExpect(status().is3xxRedirection());

        // 建立缓存
        regionService.getRegionTree();
        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache.get(getCacheKey())).isNotNull();

        // 通过 API 更新区域（使用 ID=1 的预设区域，避免查找刚创建的区域ID）
        mockMvc.perform(post("/system/regions/1")
                        .session(adminSession)
                        .param("id", "1")
                        .param("regionCode", "630000")
                        .param("regionName", "更新后区域名-" + System.currentTimeMillis())
                        .param("regionLevel", "1")
                        .param("parentId", "0"))
                .andExpect(status().is3xxRedirection());

        // 验证缓存已被清空
        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void regionCacheShouldBeEvictedAfterDelete() throws Exception {
        // 先创建一个可删除的测试区域
        String regionCode = "DEL" + System.currentTimeMillis();
        mockMvc.perform(post("/system/regions")
                        .session(adminSession)
                        .param("regionCode", regionCode)
                        .param("regionName", "待删除区域")
                        .param("regionLevel", "1")
                        .param("parentId", "0"))
                .andExpect(status().is3xxRedirection());

        // 建立缓存
        regionService.getRegionTree();
        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache.get(getCacheKey())).isNotNull();

        // 查找刚创建的区域ID
        List<Region> allRegions = regionService.getFlatRegions();
        Region createdRegion = allRegions.stream()
                .filter(r -> regionCode.equals(r.getRegionCode()))
                .findFirst()
                .orElseThrow();

        // 通过 API 删除区域
        mockMvc.perform(post("/system/regions/" + createdRegion.getId() + "/delete")
                        .session(adminSession))
                .andExpect(status().is3xxRedirection());

        // 验证缓存已被清空
        assertThat(cache.get(getCacheKey())).isNull();
    }

    @Test
    void apiRegionsShouldReturnCachedData() throws Exception {
        // 先通过 Service 建立缓存
        regionService.getRegionTree();
        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache.get(getCacheKey())).isNotNull();

        // 通过 API 访问，应该返回缓存数据
        mockMvc.perform(get("/api/regions").session(adminSession))
                .andExpect(status().isOk());

        // 缓存应该仍然存在（API 调用不会清缓存）
        assertThat(cache.get(getCacheKey())).isNotNull();
    }
}
