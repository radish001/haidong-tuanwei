(() => {
    const SELECTOR = "select[data-multiselect]";
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

    const initSelect = (select) => {
        if (!(select instanceof HTMLSelectElement) || select.tomselect) {
            return;
        }

        if (typeof window.TomSelect !== "function") {
            return;
        }

        const placeholder = select.dataset.placeholder || "请选择";
        const tom = new window.TomSelect(select, {
            plugins: ["remove_button", "dropdown_input"],
            create: false,
            persist: false,
            hideSelected: true,
            closeAfterSelect: false,
            maxItems: null,
            maxOptions: null,
            placeholder,
            searchField: ["text"],
            render: {
                no_results: () => "<div class=\"no-results\">未找到匹配项</div>"
            }
        });

        tom.wrapper.classList.add("job-multiselect-wrapper");
    };

    const init = async (scope = document) => {
        await ensureTomSelect();
        scope.querySelectorAll(SELECTOR).forEach(initSelect);
    };

    window.JobFormMultiSelect = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
