package com.haidong.tuanwei.system.support;

import com.haidong.tuanwei.system.dao.RegionDao;
import com.haidong.tuanwei.system.entity.Region;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionSelectionSupport {

    private static final String DEFAULT_DELIMITER = "-";
    private static final String EXCEL_DELIMITER = " / ";

    private final RegionDao regionDao;

    public RegionSelection normalize(String provinceCode, String cityCode, String countyCode, String fieldLabel) {
        String normalizedProvinceCode = normalizeCode(provinceCode);
        String normalizedCityCode = normalizeCode(cityCode);
        String normalizedCountyCode = normalizeCode(countyCode);

        Region province = requireRegion(normalizedProvinceCode, 1, fieldLabel + "省级区域");
        Region city = requireRegion(normalizedCityCode, 2, fieldLabel + "市级区域");
        Region county = requireRegion(normalizedCountyCode, 3, fieldLabel + "区县级区域");

        if (normalizedCityCode != null && normalizedProvinceCode == null) {
            throw new IllegalStateException(fieldLabel + "选择市级区域时必须先选择省级区域");
        }
        if (normalizedCountyCode != null && normalizedCityCode == null) {
            throw new IllegalStateException(fieldLabel + "选择区县级区域时必须先选择市级区域");
        }
        if (city != null && !city.getParentId().equals(province.getId())) {
            throw new IllegalStateException(fieldLabel + "的省市层级关系不合法");
        }
        if (county != null && !county.getParentId().equals(city.getId())) {
            throw new IllegalStateException(fieldLabel + "的市区县层级关系不合法");
        }

        return new RegionSelection(normalizedProvinceCode, normalizedCityCode, normalizedCountyCode);
    }

    public RegionSelection fromSelectedCode(String selectedCode, String fieldLabel) {
        String normalizedCode = normalizeCode(selectedCode);
        if (normalizedCode == null) {
            return new RegionSelection(null, null, null);
        }
        Region region = regionDao.findByCode(normalizedCode);
        if (region == null) {
            throw new IllegalStateException(fieldLabel + "不存在");
        }
        return switch (region.getRegionLevel()) {
            case 1 -> new RegionSelection(region.getRegionCode(), null, null);
            case 2 -> {
                Region province = regionDao.findById(region.getParentId());
                if (province == null) {
                    throw new IllegalStateException(fieldLabel + "上级区域不存在");
                }
                yield new RegionSelection(province.getRegionCode(), region.getRegionCode(), null);
            }
            case 3 -> {
                Region city = regionDao.findById(region.getParentId());
                if (city == null) {
                    throw new IllegalStateException(fieldLabel + "上级区域不存在");
                }
                Region province = regionDao.findById(city.getParentId());
                if (province == null) {
                    throw new IllegalStateException(fieldLabel + "上级区域不存在");
                }
                yield new RegionSelection(province.getRegionCode(), city.getRegionCode(), region.getRegionCode());
            }
            default -> throw new IllegalStateException(fieldLabel + "层级不合法");
        };
    }

    public String buildFullName(String provinceCode, String cityCode, String countyCode) {
        return buildPath(provinceCode, cityCode, countyCode, DEFAULT_DELIMITER);
    }

    public String buildExcelPath(String provinceCode, String cityCode, String countyCode) {
        return buildPath(provinceCode, cityCode, countyCode, EXCEL_DELIMITER);
    }

    private String buildPath(String provinceCode, String cityCode, String countyCode, String delimiter) {
        RegionSelection selection = normalize(provinceCode, cityCode, countyCode, "区域");
        String provinceName = regionName(selection.getProvinceCode());
        String cityName = regionName(selection.getCityCode());
        String countyName = regionName(selection.getCountyCode());
        if (provinceName == null) {
            return "";
        }
        if (countyName != null) {
            return provinceName + delimiter + cityName + delimiter + countyName;
        }
        if (cityName != null) {
            return provinceName + delimiter + cityName;
        }
        return provinceName;
    }

    private Region requireRegion(String code, int expectedLevel, String label) {
        if (code == null) {
            return null;
        }
        Region region = regionDao.findByCode(code);
        if (region == null || region.getRegionLevel() == null || region.getRegionLevel() != expectedLevel) {
            throw new IllegalStateException(label + "不存在");
        }
        return region;
    }

    private String regionName(String code) {
        if (code == null) {
            return null;
        }
        Region region = regionDao.findByCode(code);
        return region == null ? null : region.getRegionName();
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return code.trim();
    }

    @Getter
    public static class RegionSelection {

        private final String provinceCode;
        private final String cityCode;
        private final String countyCode;

        public RegionSelection(String provinceCode, String cityCode, String countyCode) {
            this.provinceCode = provinceCode;
            this.cityCode = cityCode;
            this.countyCode = countyCode;
        }

        public String getSelectedCode() {
            if (countyCode != null) {
                return countyCode;
            }
            if (cityCode != null) {
                return cityCode;
            }
            return provinceCode;
        }
    }
}
