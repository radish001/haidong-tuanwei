(() => {
    const SELECTOR = "[data-region-step]";
    const DEFAULT_API_URL = "/api/regions";
    const PLACEHOLDER = "请选择地区";
    const cache = new Map();

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

    const getHiddenInputs = (container) => ({
        province: container.querySelector("input[data-region-province]"),
        city: container.querySelector("input[data-region-city]"),
        county: container.querySelector("input[data-region-county]")
    });

    const getPlaceholder = (container) => container.dataset.regionPlaceholder || PLACEHOLDER;

    const getSelectionFromInputs = (container) => {
        const inputs = getHiddenInputs(container);
        return {
            province: inputs.province ? inputs.province.value : "",
            city: inputs.city ? inputs.city.value : "",
            county: inputs.county ? inputs.county.value : ""
        };
    };

    const setSelection = (container, selection) => {
        const inputs = getHiddenInputs(container);
        if (inputs.province) inputs.province.value = selection.province || "";
        if (inputs.city) inputs.city.value = selection.city || "";
        if (inputs.county) inputs.county.value = selection.county || "";
        container._regionSelection = {
            province: selection.province || "",
            city: selection.city || "",
            county: selection.county || ""
        };
    };

    const setPreviewSelection = (container, selection) => {
        container._regionPreviewSelection = {
            province: selection.province || "",
            city: selection.city || "",
            county: selection.county || ""
        };
    };

    const isSameSelection = (left, right) => {
        const a = left || {};
        const b = right || {};
        return (a.province || "") === (b.province || "")
            && (a.city || "") === (b.city || "")
            && (a.county || "") === (b.county || "");
    };

    const findPathByCode = (nodes, code, path = []) => {
        if (!code) {
            return [];
        }
        for (const node of nodes || []) {
            const nextPath = [...path, node];
            if (node.regionCode === code) {
                return nextPath;
            }
            if (node.children && node.children.length > 0) {
                const found = findPathByCode(node.children, code, nextPath);
                if (found.length > 0) {
                    return found;
                }
            }
        }
        return [];
    };

    const getSelectionPath = (container, regionTree, usePreview = false) => {
        const selection = usePreview
            ? (container._regionPreviewSelection || container._regionSelection || getSelectionFromInputs(container))
            : (container._regionSelection || getSelectionFromInputs(container));
        const deepestCode = selection.county || selection.city || selection.province;
        return findPathByCode(regionTree, deepestCode);
    };

    const renderOption = ({ level, item, provinceCode = "", cityCode = "", activeCode = "" }) => {
        const hasChildren = Array.isArray(item.children) && item.children.length > 0;
        const isActive = item.regionCode === activeCode;
        const province = level === "province" ? item.regionCode : provinceCode;
        const city = level === "city" ? item.regionCode : cityCode;
        const county = level === "county" ? item.regionCode : "";
        return `
            <button type="button"
                    class="region-panel-option${isActive ? " is-active" : ""}"
                    data-region-option
                    data-level="${level}"
                    data-has-children="${hasChildren ? "true" : "false"}"
                    data-province-code="${province}"
                    data-city-code="${city}"
                    data-county-code="${county}">
                <span class="region-panel-option-label">${item.regionName}</span>
                ${hasChildren ? '<span class="region-panel-option-arrow">></span>' : ""}
            </button>
        `;
    };

    const renderColumn = (title, items, optionsHtml) => `
        <div class="region-panel-column">
            <div class="region-panel-column-title">${title}</div>
            <div class="region-panel-options">
                ${items.length > 0 ? optionsHtml : '<div class="region-panel-empty">暂无数据</div>'}
            </div>
        </div>
    `;

    const positionDropdown = (root) => {
        const panel = root.querySelector("[data-region-panel]");
        if (!panel || panel.hidden) {
            return;
        }

        const viewportWidth = window.innerWidth || document.documentElement.clientWidth || 0;
        const margin = 12;
        const maxWidth = Math.max(280, viewportWidth - margin * 2);

        panel.style.left = "0";
        panel.style.right = "auto";
        panel.style.transform = "translateX(0)";
        panel.style.maxWidth = `${maxWidth}px`;

        const rect = panel.getBoundingClientRect();
        let shift = 0;

        if (rect.right > viewportWidth - margin) {
            shift = viewportWidth - margin - rect.right;
        }
        if (rect.left + shift < margin) {
            shift += margin - (rect.left + shift);
        }

        panel.style.transform = `translateX(${shift}px)`;
    };

    const renderPanel = (container, regionTree) => {
        const root = container.querySelector("[data-region-panel-root]");
        const trigger = root.querySelector("[data-region-trigger]");
        const text = root.querySelector("[data-region-trigger-text]");
        const clear = root.querySelector("[data-region-clear]");
        const panel = root.querySelector("[data-region-panel]");
        const selectedPath = getSelectionPath(container, regionTree, false);
        const previewPath = getSelectionPath(container, regionTree, true);
        const currentProvince = previewPath[0];
        const currentCity = previewPath[1];
        const currentCounty = previewPath[2];
        const placeholder = getPlaceholder(container);
        const displayText = selectedPath.length > 0
            ? selectedPath.map((item) => item.regionName).join(" / ")
            : placeholder;

        text.textContent = displayText;
        trigger.classList.toggle("is-placeholder", selectedPath.length === 0);
        clear.hidden = selectedPath.length === 0;

        const provinceOptions = (regionTree || [])
            .map((item) => renderOption({
                level: "province",
                item,
                activeCode: currentProvince ? currentProvince.regionCode : ""
            }))
            .join("");

        let panelHtml = renderColumn("省", regionTree || [], provinceOptions);

        if (currentProvince && Array.isArray(currentProvince.children) && currentProvince.children.length > 0) {
            const cityOptions = currentProvince.children
                .map((item) => renderOption({
                    level: "city",
                    item,
                    provinceCode: currentProvince.regionCode,
                    activeCode: currentCity ? currentCity.regionCode : ""
                }))
                .join("");
            panelHtml += renderColumn("市", currentProvince.children, cityOptions);
        }

        if (currentCity && Array.isArray(currentCity.children) && currentCity.children.length > 0) {
            const countyOptions = currentCity.children
                .map((item) => renderOption({
                    level: "county",
                    item,
                    provinceCode: currentProvince.regionCode,
                    cityCode: currentCity.regionCode,
                    activeCode: currentCounty ? currentCounty.regionCode : ""
                }))
                .join("");
            panelHtml += renderColumn("区县", currentCity.children, countyOptions);
        }

        panel.innerHTML = `
            <div class="region-panel-columns">${panelHtml}</div>
            <div class="region-panel-footer">
                <button type="button" class="ghost-button small-button" data-region-clear-panel>清空</button>
            </div>
        `;

        if (root.classList.contains("is-open") && !panel.hidden) {
            positionDropdown(root);
        }
    };

    const closeAll = (exceptContainer = null) => {
        document.querySelectorAll(`${SELECTOR} [data-region-panel-root].is-open`).forEach((root) => {
            if (exceptContainer && exceptContainer.contains(root)) {
                return;
            }
            root.classList.remove("is-open");
            const panel = root.querySelector("[data-region-panel]");
            if (panel) {
                panel.hidden = true;
            }
        });
    };

    const ensureRoot = (container) => {
        let root = container.querySelector("[data-region-panel-root]");
        if (root) {
            return root;
        }

        root = document.createElement("div");
        root.className = "region-panel-select";
        root.setAttribute("data-region-panel-root", "");
        root.innerHTML = `
            <div class="region-panel-trigger-wrap">
                <button type="button" class="region-panel-trigger is-placeholder" data-region-trigger>
                    <span data-region-trigger-text>${getPlaceholder(container)}</span>
                    <span class="region-panel-trigger-actions">
                        <span class="region-panel-clear" data-region-clear hidden>×</span>
                        <span class="region-panel-caret">v</span>
                    </span>
                </button>
            </div>
            <div class="region-panel-dropdown" data-region-panel hidden></div>
        `;

        const anchor = container.querySelector("input[data-region-province]") || null;
        container.insertBefore(root, anchor);
        return root;
    };

    const bindEvents = (container, regionTree) => {
        const root = ensureRoot(container);
        if (root.dataset.bound === "true") {
            return;
        }

        const commitOptionSelection = (option) => {
            const selection = {
                province: option.dataset.provinceCode || "",
                city: option.dataset.cityCode || "",
                county: option.dataset.countyCode || ""
            };
            setSelection(container, selection);
            setPreviewSelection(container, selection);
            renderPanel(container, regionTree);
            root.classList.remove("is-open");
            root.querySelector("[data-region-panel]").hidden = true;
        };

        root.addEventListener("mouseover", (event) => {
            const option = event.target.closest("[data-region-option]");
            if (!option || option.contains(event.relatedTarget)) {
                return;
            }

            const previewSelection = {
                province: option.dataset.provinceCode || "",
                city: option.dataset.cityCode || "",
                county: option.dataset.countyCode || ""
            };
            if (isSameSelection(container._regionPreviewSelection, previewSelection)) {
                return;
            }
            setPreviewSelection(container, previewSelection);
            renderPanel(container, regionTree);
            root.classList.add("is-open");
            root.querySelector("[data-region-panel]").hidden = false;
            positionDropdown(root);
        });

        root.addEventListener("mousedown", (event) => {
            const option = event.target.closest("[data-region-option]");
            if (!option) {
                return;
            }
            event.preventDefault();
            event.stopPropagation();
            commitOptionSelection(option);
        });

        root.addEventListener("click", (event) => {
            const clearTrigger = event.target.closest("[data-region-clear]");
            if (clearTrigger) {
                event.preventDefault();
                event.stopPropagation();
                setSelection(container, { province: "", city: "", county: "" });
                setPreviewSelection(container, { province: "", city: "", county: "" });
                renderPanel(container, regionTree);
                return;
            }

            const trigger = event.target.closest("[data-region-trigger]");
            if (trigger) {
                event.preventDefault();
                const isOpen = root.classList.contains("is-open");
                closeAll(container);
                setPreviewSelection(container, container._regionSelection || getSelectionFromInputs(container));
                root.classList.toggle("is-open", !isOpen);
                root.querySelector("[data-region-panel]").hidden = isOpen;
                if (!isOpen) {
                    renderPanel(container, regionTree);
                    positionDropdown(root);
                }
                return;
            }

            const clearButton = event.target.closest("[data-region-clear-panel]");
            if (clearButton) {
                event.preventDefault();
                setSelection(container, { province: "", city: "", county: "" });
                setPreviewSelection(container, { province: "", city: "", county: "" });
                renderPanel(container, regionTree);
                return;
            }

            const option = event.target.closest("[data-region-option]");
            if (!option) {
                return;
            }

            event.preventDefault();
            event.stopPropagation();
        });

        root.dataset.bound = "true";
    };

    const initContainer = async (container) => {
        const apiUrl = container.dataset.regionApiUrl || DEFAULT_API_URL;
        const regionTree = await fetchRegionTree(apiUrl);
        ensureRoot(container);
        setSelection(container, getSelectionFromInputs(container));
        setPreviewSelection(container, container._regionSelection);
        bindEvents(container, regionTree);
        renderPanel(container, regionTree);
    };

    const init = async (root = document) => {
        const containers = Array.from(root.querySelectorAll(SELECTOR));
        for (const container of containers) {
            await initContainer(container);
        }
    };

    document.addEventListener("click", (event) => {
        if (event.target.closest(SELECTOR)) {
            return;
        }
        closeAll();
        document.querySelectorAll(`${SELECTOR} [data-region-panel]`).forEach((panel) => {
            panel.hidden = true;
        });
    });

    document.addEventListener("keydown", (event) => {
        if (event.key !== "Escape") {
            return;
        }
        closeAll();
        document.querySelectorAll(`${SELECTOR} [data-region-panel]`).forEach((panel) => {
            panel.hidden = true;
        });
    });

    window.addEventListener("resize", () => {
        document.querySelectorAll(`${SELECTOR} [data-region-panel-root].is-open`).forEach((root) => {
            positionDropdown(root);
        });
    });

    window.RegionStepSelect = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
