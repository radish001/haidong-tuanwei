(() => {
    const ROOT_SELECTOR = "[data-multi-select-root]";
    const TRIGGER_SELECTOR = "[data-multi-select-trigger]";
    const PANEL_SELECTOR = "[data-multi-select-panel]";
    const TEXT_SELECTOR = "[data-multi-select-text]";
    const PLACEHOLDER_SELECTOR = "[data-multi-select-placeholder]";
    const SEARCH_SELECTOR = "[data-multi-select-search]";

    const updateSummary = (root) => {
        const text = root.querySelector(TEXT_SELECTOR);
        const placeholder = root.querySelector(PLACEHOLDER_SELECTOR);
        const checked = Array.from(root.querySelectorAll('input[type="checkbox"]:checked'));
        if (!text) {
            return;
        }

        text.innerHTML = "";

        if (checked.length === 0) {
            if (placeholder instanceof HTMLElement) {
                placeholder.hidden = false;
            }
            root.querySelector(TRIGGER_SELECTOR)?.classList.add("is-placeholder");
            return;
        }

        if (placeholder instanceof HTMLElement) {
            placeholder.hidden = true;
        }

        checked.forEach((input) => {
            const label = input.closest("label")?.querySelector("span")?.textContent?.trim();
            if (!label) {
                return;
            }

            const tag = document.createElement("span");
            tag.className = "multi-dropdown-tag";

            const tagLabel = document.createElement("span");
            tagLabel.className = "multi-dropdown-tag-label";
            tagLabel.textContent = label;

            const tagRemove = document.createElement("span");
            tagRemove.className = "multi-dropdown-tag-remove";
            tagRemove.setAttribute("data-multi-select-remove", input.value);
            tagRemove.setAttribute("role", "button");
            tagRemove.setAttribute("tabindex", "0");
            tagRemove.setAttribute("aria-label", `移除${label}`);
            tagRemove.textContent = "×";

            tag.append(tagLabel, tagRemove);
            text.appendChild(tag);
        });

        root.querySelector(TRIGGER_SELECTOR)?.classList.remove("is-placeholder");
    };

    const filterOptions = (root) => {
        const search = root.querySelector(SEARCH_SELECTOR);
        const keyword = search instanceof HTMLInputElement ? search.value.trim().toLowerCase() : "";

        root.querySelectorAll(".checkbox-option").forEach((option) => {
            const label = option.querySelector("span")?.textContent?.trim().toLowerCase() || "";
            option.hidden = Boolean(keyword) && !label.includes(keyword);
        });
    };

    const closeAll = (exceptRoot = null) => {
        document.querySelectorAll(`${ROOT_SELECTOR}.is-open`).forEach((root) => {
            if (exceptRoot && root === exceptRoot) {
                return;
            }
            root.classList.remove("is-open");
            const panel = root.querySelector(PANEL_SELECTOR);
            if (panel) {
                panel.hidden = true;
            }
        });
    };

    const bindRoot = (root) => {
        if (!(root instanceof HTMLElement) || root.dataset.bound === "true") {
            return;
        }

        const trigger = root.querySelector(TRIGGER_SELECTOR);
        const panel = root.querySelector(PANEL_SELECTOR);
        if (!(trigger instanceof HTMLButtonElement) || !(panel instanceof HTMLElement)) {
            return;
        }

        updateSummary(root);
        filterOptions(root);

        trigger.addEventListener("click", (event) => {
            const removeTarget = event.target instanceof HTMLElement
                ? event.target.closest("[data-multi-select-remove]")
                : null;

            if (removeTarget instanceof HTMLElement) {
                event.preventDefault();
                event.stopPropagation();
                const value = removeTarget.getAttribute("data-multi-select-remove");
                const checkbox = Array.from(root.querySelectorAll('input[type="checkbox"]'))
                    .find((input) => input.value === value);
                if (checkbox instanceof HTMLInputElement) {
                    checkbox.checked = false;
                    updateSummary(root);
                }
                return;
            }

            event.preventDefault();
            const isOpen = root.classList.contains("is-open");
            closeAll(root);
            root.classList.toggle("is-open", !isOpen);
            panel.hidden = isOpen;
            if (!isOpen) {
                root.querySelector(SEARCH_SELECTOR)?.focus();
            }
        });

        root.addEventListener("change", (event) => {
            if (event.target instanceof HTMLInputElement && event.target.type === "checkbox") {
                updateSummary(root);
            }
        });

        root.querySelector(SEARCH_SELECTOR)?.addEventListener("input", () => {
            filterOptions(root);
        });

        root.addEventListener("keydown", (event) => {
            const removeTarget = event.target instanceof HTMLElement
                ? event.target.closest("[data-multi-select-remove]")
                : null;
            if (!(removeTarget instanceof HTMLElement)) {
                return;
            }
            if (event.key !== "Enter" && event.key !== " ") {
                return;
            }
            event.preventDefault();
            removeTarget.click();
        });

        root.dataset.bound = "true";
    };

    const init = (scope = document) => {
        scope.querySelectorAll(ROOT_SELECTOR).forEach(bindRoot);
    };

    document.addEventListener("click", (event) => {
        if (event.target.closest(ROOT_SELECTOR)) {
            return;
        }
        closeAll();
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeAll();
        }
    });

    window.JobFormMultiSelect = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
