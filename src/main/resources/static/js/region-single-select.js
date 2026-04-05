(() => {
    const SELECTOR = "[data-region-single]";
    const DEFAULT_API_URL = "/api/regions";

    let regionTreeCache = null;

    const fetchRegionTree = async (apiUrl) => {
        if (regionTreeCache) {
            return regionTreeCache;
        }
        const response = await fetch(apiUrl, {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        });
        if (!response.ok) {
            throw new Error(`Failed to load regions: ${response.status}`);
        }
        regionTreeCache = await response.json();
        return regionTreeCache;
    };

    // 构建扁平化的级联选项列表
    const buildCascadedOptions = (regionTree, level = 1, prefix = "") => {
        const options = [];
        regionTree.forEach(item => {
            const indent = "　".repeat(level - 1); // 使用全角空格缩进
            const label = prefix ? `${prefix} / ${item.regionName}` : item.regionName;
            const displayLabel = indent + item.regionName;

            // 添加当前级别的选项
            options.push({
                value: item.regionCode,
                label: displayLabel,
                fullLabel: label,
                level: level,
                hasChildren: item.children && item.children.length > 0
            });

            // 递归添加子级
            if (item.children && item.children.length > 0) {
                options.push(...buildCascadedOptions(item.children, level + 1, label));
            }
        });
        return options;
    };

    // 根据选中的 code 推断出省、市、县三级 code
    const parseSelectedCode = (selectedCode, regionTree) => {
        if (!selectedCode) {
            return { province: "", city: "", county: "" };
        }

        // 在树中查找选中的节点
        const findNode = (nodes, code, path = []) => {
            for (const node of nodes) {
                if (node.regionCode === code) {
                    return [...path, node];
                }
                if (node.children) {
                    const found = findNode(node.children, code, [...path, node]);
                    if (found) return found;
                }
            }
            return null;
        };

        const path = findNode(regionTree, selectedCode);
        if (!path) {
            return { province: "", city: "", county: "" };
        }

        // 根据路径长度返回对应的省、市、县
        return {
            province: path[0]?.regionCode || "",
            city: path[1]?.regionCode || "",
            county: path[2]?.regionCode || ""
        };
    };

    // 初始化单个选择器
    const initSelect = async (container) => {
        const select = container.querySelector("select[data-region-select]");
        if (!select) return;

        const apiUrl = container.dataset.regionApiUrl || DEFAULT_API_URL;
        const regionTree = await fetchRegionTree(apiUrl);

        // 获取当前值（可能来自编辑模式）
        const provinceCode = container.dataset.provinceCode || "";
        const cityCode = container.dataset.cityCode || "";
        const countyCode = container.dataset.countyCode || "";

        // 确定当前选中的最深层级 code
        let selectedCode = countyCode || cityCode || provinceCode || "";

        // 构建选项
        const options = buildCascadedOptions(regionTree);

        // 生成 HTML
        let html = '<option value="">请选择地区</option>';
        options.forEach(opt => {
            const selected = opt.value === selectedCode ? ' selected' : '';
            // 有子级的选项禁用（强制用户选择到最底层或选择中间层级）
            // 但这里我们不强制，让用户可以选择任意层级
            html += `<option value="${opt.value}"${selected}>${opt.label}</option>`;
        });
        select.innerHTML = html;

        // 存储 regionTree 供后续使用
        container._regionTree = regionTree;

        // 同步隐藏字段
        syncHiddenFields(container, selectedCode);
    };

    // 同步隐藏字段值
    const syncHiddenFields = (container, selectedCode) => {
        const provinceInput = container.querySelector('input[data-region-province]');
        const cityInput = container.querySelector('input[data-region-city]');
        const countyInput = container.querySelector('input[data-region-county]');

        if (!provinceInput) return;

        const codes = parseSelectedCode(selectedCode, container._regionTree || []);

        provinceInput.value = codes.province;
        if (cityInput) cityInput.value = codes.city;
        if (countyInput) countyInput.value = codes.county;
    };

    // 初始化所有选择器
    const init = async (root = document) => {
        const containers = Array.from(root.querySelectorAll(SELECTOR));
        for (const container of containers) {
            await initSelect(container);
        }
    };

    // 监听变化事件
    document.addEventListener("change", (event) => {
        const select = event.target.closest("[data-region-select]");
        if (!select) return;

        const container = select.closest(SELECTOR);
        if (!container) return;

        syncHiddenFields(container, select.value);
    });

    window.RegionSingleSelect = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
