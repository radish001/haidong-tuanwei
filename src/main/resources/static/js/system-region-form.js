(() => {
    const FORM_SELECTOR = "[data-region-form]";
    const LEVEL_SELECTOR = "[data-region-level-select]";
    const PARENT_CONTAINER_SELECTOR = "[data-region-parent-container]";
    const PARENT_SELECT_SELECTOR = "[data-region-parent-select]";

    const syncRegionParentOptions = (form) => {
        if (!(form instanceof HTMLFormElement)) {
            return;
        }

        const levelSelect = form.querySelector(LEVEL_SELECTOR);
        const parentContainer = form.querySelector(PARENT_CONTAINER_SELECTOR);
        const parentSelect = form.querySelector(PARENT_SELECT_SELECTOR);
        if (!(levelSelect instanceof HTMLSelectElement)
                || !(parentContainer instanceof HTMLElement)
                || !(parentSelect instanceof HTMLSelectElement)) {
            return;
        }

        const selectedParentId = form.dataset.selectedParentId || "";
        const level = Number.parseInt(levelSelect.value || "1", 10);
        const requiredParentLevel = level - 1;

        Array.from(parentSelect.options).forEach((option) => {
            if (option.value === "0") {
                option.hidden = level !== 1;
                return;
            }
            option.hidden = option.dataset.parentLevel !== String(requiredParentLevel);
        });

        if (level <= 1) {
            parentContainer.hidden = true;
            parentSelect.value = "0";
        } else {
            parentContainer.hidden = false;
            const canKeepSelected = Array.from(parentSelect.options).some((option) => {
                return !option.hidden && option.value === selectedParentId;
            });
            if (canKeepSelected) {
                parentSelect.value = selectedParentId;
            } else if (parentSelect.selectedOptions.length === 0 || parentSelect.selectedOptions[0].hidden) {
                parentSelect.value = "";
            }
        }
    };

    const init = (root = document) => {
        root.querySelectorAll(FORM_SELECTOR).forEach((form) => syncRegionParentOptions(form));
    };

    document.addEventListener("change", (event) => {
        const levelSelect = event.target.closest(LEVEL_SELECTOR);
        if (!(levelSelect instanceof HTMLSelectElement)) {
            return;
        }
        const form = levelSelect.closest(FORM_SELECTOR);
        syncRegionParentOptions(form);
    });

    window.SystemRegionForm = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
