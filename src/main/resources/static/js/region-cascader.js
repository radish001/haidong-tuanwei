(() => {
    const CASCADER_SELECTOR = "[data-region-cascader]";
    const PROVINCE_SELECTOR = "[data-region-province]";
    const CITY_SELECTOR = "[data-region-city]";
    const COUNTY_SELECTOR = "[data-region-county]";
    const DEFAULT_API_URL = "/api/regions";

    let regionTreePromise = null;

    const fetchRegionTree = async (apiUrl) => {
        if (!regionTreePromise) {
            regionTreePromise = fetch(apiUrl, {
                headers: {
                    "X-Requested-With": "XMLHttpRequest"
                }
            }).then((response) => {
                if (!response.ok) {
                    throw new Error(`Failed to load regions: ${response.status}`);
                }
                return response.json();
            });
        }
        return regionTreePromise;
    };

    const buildOptions = (select, items, placeholder, selectedValue) => {
        if (!(select instanceof HTMLSelectElement)) {
            return;
        }
        const options = [`<option value="">${placeholder}</option>`];
        items.forEach((item) => {
            const selected = item.regionCode === selectedValue ? " selected" : "";
            options.push(`<option value="${item.regionCode}"${selected}>${item.regionName}</option>`);
        });
        select.innerHTML = options.join("");
    };

    const syncCascader = async (container) => {
        if (!(container instanceof HTMLElement)) {
            return;
        }
        const provinceSelect = container.querySelector(PROVINCE_SELECTOR);
        const citySelect = container.querySelector(CITY_SELECTOR);
        const countySelect = container.querySelector(COUNTY_SELECTOR);
        if (!(provinceSelect instanceof HTMLSelectElement)
                || !(citySelect instanceof HTMLSelectElement)
                || !(countySelect instanceof HTMLSelectElement)) {
            return;
        }

        const apiUrl = container.dataset.regionApiUrl || DEFAULT_API_URL;
        const regionTree = await fetchRegionTree(apiUrl);

        // 保存当前选中的值（用于在重建选项后恢复）
        const currentProvinceValue = provinceSelect.value;
        const currentCityValue = citySelect.value;
        const currentCountyValue = countySelect.value;

        // 首次初始化时从 data-selected-value 读取，否则使用当前值
        const selectedProvinceCode = provinceSelect.dataset.selectedValue || currentProvinceValue || "";
        const selectedCityCode = citySelect.dataset.selectedValue || currentCityValue || "";
        const selectedCountyCode = countySelect.dataset.selectedValue || currentCountyValue || "";

        // 构建省选项
        buildOptions(provinceSelect, regionTree, "请选择", selectedProvinceCode);

        // 根据选中的省构建市选项
        const province = regionTree.find((item) => item.regionCode === provinceSelect.value);
        const cities = province && Array.isArray(province.children) ? province.children : [];
        buildOptions(citySelect, cities, "请选择", selectedCityCode);
        citySelect.disabled = cities.length === 0;

        // 根据选中的市构建区县选项
        const city = cities.find((item) => item.regionCode === citySelect.value);
        const counties = city && Array.isArray(city.children) ? city.children : [];
        buildOptions(countySelect, counties, "请选择", selectedCountyCode);
        countySelect.disabled = counties.length === 0;

        // 同步当前值到 dataset，供下次使用
        provinceSelect.dataset.selectedValue = provinceSelect.value;
        citySelect.dataset.selectedValue = citySelect.value;
        countySelect.dataset.selectedValue = countySelect.value;
    };

    const init = async (root = document) => {
        const cascaders = Array.from(root.querySelectorAll(CASCADER_SELECTOR));
        for (const cascader of cascaders) {
            // 在初始化前，先将当前选中的值同步到 dataset
            // 这样即使在动态加载的抽屉中也能保留已选值
            const provinceSelect = cascader.querySelector(PROVINCE_SELECTOR);
            const citySelect = cascader.querySelector(CITY_SELECTOR);
            const countySelect = cascader.querySelector(COUNTY_SELECTOR);
            if (provinceSelect && provinceSelect.value) {
                provinceSelect.dataset.selectedValue = provinceSelect.value;
            }
            if (citySelect && citySelect.value) {
                citySelect.dataset.selectedValue = citySelect.value;
            }
            if (countySelect && countySelect.value) {
                countySelect.dataset.selectedValue = countySelect.value;
            }
            await syncCascader(cascader);
        }
    };

    document.addEventListener("change", async (event) => {
        const changedSelect = event.target;
        if (!(changedSelect instanceof HTMLSelectElement)) {
            return;
        }

        // 检查是否是省选择框变化
        if (changedSelect.matches(PROVINCE_SELECTOR)) {
            const cascader = changedSelect.closest(CASCADER_SELECTOR);
            if (cascader instanceof HTMLElement) {
                const citySelect = cascader.querySelector(CITY_SELECTOR);
                const countySelect = cascader.querySelector(COUNTY_SELECTOR);
                // 清空市和县的值，让 syncCascader 重新构建选项
                if (citySelect instanceof HTMLSelectElement) {
                    citySelect.dataset.selectedValue = "";
                    citySelect.value = "";
                }
                if (countySelect instanceof HTMLSelectElement) {
                    countySelect.dataset.selectedValue = "";
                    countySelect.value = "";
                }
                // 更新当前省的 dataset，然后重新同步
                changedSelect.dataset.selectedValue = changedSelect.value;
                await syncCascader(cascader);
            }
            return;
        }

        // 检查是否是市选择框变化
        if (changedSelect.matches(CITY_SELECTOR)) {
            const cascader = changedSelect.closest(CASCADER_SELECTOR);
            if (cascader instanceof HTMLElement) {
                const countySelect = cascader.querySelector(COUNTY_SELECTOR);
                // 清空县的值
                if (countySelect instanceof HTMLSelectElement) {
                    countySelect.dataset.selectedValue = "";
                    countySelect.value = "";
                }
                // 更新当前市的 dataset，然后重新同步
                changedSelect.dataset.selectedValue = changedSelect.value;
                await syncCascader(cascader);
            }
        }
    });

    window.RegionCascader = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
