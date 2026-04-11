(() => {
    const SELECTOR = ".filter-form select:not([multiple]), .edit-form select:not([multiple])";
    const MOBILE_BREAKPOINT = 1180;
    const TOM_SELECT_SCRIPT_URL = "https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js";
    let tomSelectLoader = null;

    const ensureTomSelect = () => {
        if (typeof window.TomSelect === "function") {
            return Promise.resolve();
        }
        if (tomSelectLoader) {
            return tomSelectLoader;
        }
        tomSelectLoader = new Promise((resolve, reject) => {
            const existingScript = document.querySelector(`script[src="${TOM_SELECT_SCRIPT_URL}"]`);
            if (existingScript) {
                existingScript.addEventListener("load", () => resolve(), { once: true });
                existingScript.addEventListener("error", reject, { once: true });
                return;
            }
            const script = document.createElement("script");
            script.src = TOM_SELECT_SCRIPT_URL;
            script.onload = () => resolve();
            script.onerror = reject;
            document.body.appendChild(script);
        });
        return tomSelectLoader;
    };

    const shouldEnhanceSelect = (select) => {
        if (!(select instanceof HTMLSelectElement)) {
            return false;
        }
        if (select.tomselect) {
            return false;
        }
        if (select.dataset.noBootstrapEnhance === "true") {
            return false;
        }
        if (select.closest("[data-region-step]")) {
            return false;
        }
        if (select.matches("[data-region-province], [data-region-city], [data-region-county]")) {
            return false;
        }
        return true;
    };

    const buildSelectConfig = (select) => {
        const optionCount = Array.from(select.options).filter((option) => option.value !== "").length;
        const searchable = optionCount >= 8
            || /school|major|enterprise|industry|degree|ethnicity|political|education/i.test(select.name || "");

        return {
            allowEmptyOption: true,
            maxItems: 1,
            maxOptions: null,
            hideSelected: false,
            create: false,
            plugins: searchable ? ["dropdown_input"] : [],
            render: {
                no_results: () => "<div class=\"no-results\">未找到匹配项</div>"
            }
        };
    };

    const isSchoolSelect = (select) => /school/i.test(`${select.name || ""} ${select.id || ""}`);

    const initSelects = async (root = document) => {
        await ensureTomSelect();
        root.querySelectorAll(SELECTOR).forEach((select) => {
            if (!shouldEnhanceSelect(select)) {
                return;
            }
            // Tom Select keeps the original select in sync, so existing form logic remains unchanged.
            const tom = new window.TomSelect(select, buildSelectConfig(select));
            if (isSchoolSelect(select)) {
                tom.wrapper.classList.add("ts-wrapper-auto-wide-dropdown");
            }
        });
    };

    const setSidebarOpen = (open) => {
        const backdrop = document.querySelector("[data-sidebar-backdrop]");
        document.body.classList.toggle("sidebar-mobile-open", open);
        if (backdrop instanceof HTMLElement) {
            backdrop.hidden = !open;
        }
    };

    const initMobileSidebar = () => {
        if (document.body.dataset.mobileSidebarReady === "true") {
            return;
        }
        const toggles = document.querySelectorAll("[data-sidebar-toggle]");
        if (toggles.length === 0) {
            return;
        }

        toggles.forEach((toggle) => {
            if (toggle.dataset.bound === "true") {
                return;
            }
            toggle.addEventListener("click", () => {
                const nextState = !document.body.classList.contains("sidebar-mobile-open");
                setSidebarOpen(nextState);
            });
            toggle.dataset.bound = "true";
        });

        document.querySelector("[data-sidebar-backdrop]")?.addEventListener("click", () => setSidebarOpen(false));
        document.querySelectorAll(".sidebar a.nav-link, .sidebar .navbar-brand").forEach((link) => {
            link.addEventListener("click", () => {
                if (window.innerWidth <= MOBILE_BREAKPOINT) {
                    setSidebarOpen(false);
                }
            });
        });

        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape") {
                setSidebarOpen(false);
            }
        });

        window.addEventListener("resize", () => {
            if (window.innerWidth > MOBILE_BREAKPOINT) {
                setSidebarOpen(false);
            }
        });
        setSidebarOpen(false);
        document.body.dataset.mobileSidebarReady = "true";
    };

    const init = async (root = document) => {
        await initSelects(root);
        initMobileSidebar();
    };

    window.BootstrapEnhancements = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
