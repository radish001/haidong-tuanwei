(() => {
    const SELECTOR = "[data-region-step]";
    const DEFAULT_API_URL = "/api/regions";
    const TREESELECT_SCRIPT_URL = "https://cdn.jsdelivr.net/npm/treeselectjs@0.14.2/dist/treeselectjs.umd.js";
    const cache = new Map();
    let treeselectLoader = null;

    const ensureTreeselect = () => {
        if (typeof window.Treeselect === "function") {
            return Promise.resolve();
        }
        if (treeselectLoader) {
            return treeselectLoader;
        }
        treeselectLoader = new Promise((resolve, reject) => {
            const existing = document.querySelector(`script[src="${TREESELECT_SCRIPT_URL}"]`);
            if (existing) {
                existing.addEventListener("load", () => resolve(), { once: true });
                existing.addEventListener("error", reject, { once: true });
                return;
            }
            const script = document.createElement("script");
            script.src = TREESELECT_SCRIPT_URL;
            script.onload = () => resolve();
            script.onerror = reject;
            document.body.appendChild(script);
        });
        return treeselectLoader;
    };

    const fetchRegionTree = async (apiUrl) => {
        if (cache.has(apiUrl)) {
            return cache.get(apiUrl);
        }
        const response = await fetch(apiUrl, {
            headers: { "X-Requested-With": "XMLHttpRequest" }
        });
        if (!response.ok) {
            throw new Error(`Failed to load regions: ${response.status}`);
        }
        const regionTree = await response.json();
        cache.set(apiUrl, regionTree);
        return regionTree;
    };

    const getInputs = (container) => ({
        province: container.querySelector("input[data-region-province]"),
        city: container.querySelector("input[data-region-city]"),
        county: container.querySelector("input[data-region-county]")
    });

    const updateHiddenInputs = (container, selection) => {
        const inputs = getInputs(container);
        if (inputs.province) {
            inputs.province.value = selection.province || "";
        }
        if (inputs.city) {
            inputs.city.value = selection.city || "";
        }
        if (inputs.county) {
            inputs.county.value = selection.county || "";
        }
    };

    const buildTreeOption = (node, path = [], pathMap = new Map()) => {
        const nextPath = [...path, node];
        pathMap.set(node.regionCode, nextPath);
        return {
            name: node.regionName,
            value: node.regionCode,
            children: Array.isArray(node.children)
                ? node.children.map((child) => buildTreeOption(child, nextPath, pathMap))
                : []
        };
    };

    const getCurrentValue = (inputs) => inputs.county.value || inputs.city.value || inputs.province.value || null;

    const resolveSelection = (path) => ({
        province: path[0]?.regionCode || "",
        city: path[1]?.regionCode || "",
        county: path[2]?.regionCode || ""
    });

    const resolveDisplayText = (path, placeholder) => {
        if (!Array.isArray(path) || path.length === 0) {
            return placeholder;
        }
        return path.map((item) => item.regionName).join(" / ");
    };

    const initContainer = async (container) => {
        if (!(container instanceof HTMLElement) || container.dataset.bootstrapEnhanced === "true") {
            return;
        }

        await ensureTreeselect();
        const apiUrl = container.dataset.regionApiUrl || DEFAULT_API_URL;
        const regionTree = await fetchRegionTree(apiUrl);
        const inputs = getInputs(container);
        if (!inputs.province || !inputs.city || !inputs.county) {
            return;
        }

        const wrapper = document.createElement("div");
        wrapper.className = "region-treeselect-shell";
        const mountPoint = document.createElement("div");
        mountPoint.className = "region-treeselect";
        wrapper.appendChild(mountPoint);

        inputs.province.parentNode.insertBefore(wrapper, inputs.province);

        if (typeof window.Treeselect !== "function") {
            return;
        }

        const pathMap = new Map();
        const options = regionTree.map((item) => buildTreeOption(item, [], pathMap));
        const initialValue = getCurrentValue(inputs);
        const placeholder = container.dataset.regionPlaceholder || "请选择地区";
        const setDisplayValue = (path) => {
            const input = mountPoint.querySelector(".treeselect-input__edit");
            if (input instanceof HTMLInputElement) {
                const hasSelection = Array.isArray(path) && path.length > 0;
                input.value = hasSelection ? resolveDisplayText(path, placeholder) : "";
                input.placeholder = placeholder;
            }
        };
        const treeselect = new window.Treeselect({
            parentHtmlContainer: mountPoint,
            value: initialValue || undefined,
            options,
            isSingleSelect: true,
            searchable: false,
            clearable: false,
            showTags: false,
            grouped: false,
            placeholder,
            emptyText: "未找到地区",
            inputCallback: (value) => {
                const selectedValue = Array.isArray(value) ? value[0] : value;
                const path = pathMap.get(selectedValue) || [];
                updateHiddenInputs(container, resolveSelection(path));
                setDisplayValue(path);
            }
        });

        const input = mountPoint.querySelector(".treeselect-input__edit");
        if (input instanceof HTMLInputElement) {
            input.placeholder = placeholder;
        }
        setDisplayValue(pathMap.get(initialValue) || []);
        mountPoint.querySelector(".treeselect-list")?.classList.add("region-treeselect-list");
        container._treeselect = treeselect;

        container.dataset.bootstrapEnhanced = "true";
    };

    const init = async (root = document) => {
        const containers = Array.from(root.querySelectorAll(SELECTOR));
        for (const container of containers) {
            await initContainer(container);
        }
    };

    window.RegionStepSelect = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
