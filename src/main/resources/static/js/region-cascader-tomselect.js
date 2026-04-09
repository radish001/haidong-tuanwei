/**
 * 级联区域选择器 - Tom Select实现
 * 替代原有的region-cascader.js和region-step-select.js
 * 省市区三级联动
 */

(() => {
  const DEFAULT_API_URL = "/api/regions";
  const CASCADER_SELECTOR = "[data-region-cascader]";

  // 缓存区域树数据
  const regionCache = new Map();

  /**
   * 获取区域树数据
   */
  const fetchRegionTree = async (apiUrl) => {
    if (regionCache.has(apiUrl)) {
      return regionCache.get(apiUrl);
    }

    try {
      const response = await fetch(apiUrl, {
        headers: { "X-Requested-With": "XMLHttpRequest" }
      });

      if (!response.ok) {
        throw new Error(`Failed to load regions: ${response.status}`);
      }

      const data = await response.json();
      regionCache.set(apiUrl, data);
      return data;
    } catch (error) {
      console.error("[RegionCascader] Failed to fetch regions:", error);
      return [];
    }
  };

  /**
   * 递归查找区域节点
   */
  const findRegionByCode = (regions, code) => {
    if (!code || !Array.isArray(regions)) return null;

    for (const region of regions) {
      if (region.regionCode === code) return region;
      if (region.children && region.children.length > 0) {
        const found = findRegionByCode(region.children, code);
        if (found) return found;
      }
    }
    return null;
  };

  /**
   * 初始化单个级联选择器
   */
  const initCascader = async (container) => {
    const apiUrl = container.dataset.regionApiUrl || DEFAULT_API_URL;
    const placeholder = container.dataset.regionPlaceholder || "请选择";

    // 获取隐藏的input元素（存储实际值）
    const provinceInput = container.querySelector("[data-region-province]");
    const cityInput = container.querySelector("[data-region-city]");
    const countyInput = container.querySelector("[data-region-county]");

    if (!provinceInput || !cityInput || !countyInput) {
      console.warn("[RegionCascader] Missing input elements:", container);
      return;
    }

    // 获取当前值
    const initialProvince = provinceInput.value;
    const initialCity = cityInput.value;
    const initialCounty = countyInput.value;

    // 创建select元素容器
    const selectWrapper = document.createElement("div");
    selectWrapper.className = "row g-2";
    container.appendChild(selectWrapper);

    // 省select
    const provinceCol = document.createElement("div");
    provinceCol.className = "col-4";
    const provinceSelect = document.createElement("select");
    provinceSelect.className = "form-select tomselect-cascade";
    provinceSelect.dataset.level = "province";
    provinceCol.appendChild(provinceSelect);
    selectWrapper.appendChild(provinceCol);

    // 市select
    const cityCol = document.createElement("div");
    cityCol.className = "col-4";
    const citySelect = document.createElement("select");
    citySelect.className = "form-select tomselect-cascade";
    citySelect.dataset.level = "city";
    cityCol.appendChild(citySelect);
    selectWrapper.appendChild(cityCol);

    // 区县select
    const countyCol = document.createElement("div");
    countyCol.className = "col-4";
    const countySelect = document.createElement("select");
    countySelect.className = "form-select tomselect-cascade";
    countySelect.dataset.level = "county";
    countyCol.appendChild(countySelect);
    selectWrapper.appendChild(countyCol);

    // 加载区域数据
    const regionTree = await fetchRegionTree(apiUrl);

    // 初始化省级Tom Select
    const provinceTom = new TomSelect(provinceSelect, {
      placeholder: placeholder + "省",
      valueField: "regionCode",
      labelField: "regionName",
      searchField: "regionName",
      maxItems: 1,
      options: regionTree,
      items: initialProvince ? [initialProvince] : [],
      onInitialize: function() {
        // 初始加载后，如果有省值，加载市数据
        if (initialProvince) {
          this.loadCityOptions(initialProvince, initialCity);
        }
      }
    });

    // 市级Tom Select
    const cityTom = new TomSelect(citySelect, {
      placeholder: placeholder + "市",
      valueField: "regionCode",
      labelField: "regionName",
      searchField: "regionName",
      maxItems: 1,
      options: [],
      items: initialCity ? [initialCity] : []
    });

    // 区县级Tom Select
    const countyTom = new TomSelect(countySelect, {
      placeholder: placeholder + "区县",
      valueField: "regionCode",
      labelField: "regionName",
      searchField: "regionName",
      maxItems: 1,
      options: [],
      items: initialCounty ? [initialCounty] : []
    });

    // 扩展provinceTom方法：加载市数据
    provinceTom.loadCityOptions = (provinceCode, selectedCityCode = null) => {
      const province = findRegionByCode(regionTree, provinceCode);
      const cities = province && province.children ? province.children : [];

      cityTom.clearOptions();
      cityTom.addOptions(cities);

      if (selectedCityCode) {
        cityTom.addItem(selectedCityCode);
        // 加载区县
        const city = findRegionByCode(cities, selectedCityCode);
        const counties = city && city.children ? city.children : [];
        countyTom.clearOptions();
        countyTom.addOptions(counties);
        if (initialCounty) {
          countyTom.addItem(initialCounty);
        }
      }
    };

    // 监听省级变化
    provinceTom.on("change", (value) => {
      provinceInput.value = value || "";

      // 清空市和区县
      cityTom.clear();
      cityTom.clearOptions();
      cityInput.value = "";

      countyTom.clear();
      countyTom.clearOptions();
      countyInput.value = "";

      if (value) {
        // 加载市数据
        const province = findRegionByCode(regionTree, value);
        const cities = province && province.children ? province.children : [];
        cityTom.addOptions(cities);
      }

      // 触发change事件
      provinceInput.dispatchEvent(new Event("change", { bubbles: true }));
    });

    // 监听市级变化
    cityTom.on("change", (value) => {
      cityInput.value = value || "";

      // 清空区县
      countyTom.clear();
      countyTom.clearOptions();
      countyInput.value = "";

      if (value) {
        // 加载区县数据
        const provinceCode = provinceTom.getValue();
        const province = findRegionByCode(regionTree, provinceCode);
        const cities = province && province.children ? province.children : [];
        const city = findRegionByCode(cities, value);
        const counties = city && city.children ? city.children : [];
        countyTom.addOptions(counties);
      }

      cityInput.dispatchEvent(new Event("change", { bubbles: true }));
    });

    // 监听区县级变化
    countyTom.on("change", (value) => {
      countyInput.value = value || "";
      countyInput.dispatchEvent(new Event("change", { bubbles: true }));
    });
  };

  /**
   * 初始化所有级联选择器
   */
  const init = async (root = document) => {
    const containers = Array.from(root.querySelectorAll(CASCADER_SELECTOR));

    for (const container of containers) {
      // 跳过已初始化的
      if (container.dataset.initialized === "true") continue;

      try {
        await initCascader(container);
        container.dataset.initialized = "true";
      } catch (error) {
        console.error("[RegionCascader] Failed to initialize:", error);
      }
    }
  };

  // 全局API
  window.RegionCascaderTomSelect = { init };

  // 自动初始化
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () => init());
  } else {
    init();
  }

  // 监听抽屉打开事件，初始化动态加载的内容
  document.addEventListener("drawer:opened", (event) => {
    if (event.detail && event.detail.container) {
      init(event.detail.container);
    }
  });
})();
