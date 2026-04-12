(() => {
    const DRAWER_PAGE_SELECTOR = "[data-drawer-page]";
    const DRAWER_PANEL_SELECTOR = "[data-drawer-offcanvas]";
    const DRAWER_CONTENT_SELECTOR = "[data-drawer-content]";
    const DRAWER_TITLE_SELECTOR = "[data-drawer-title]";
    const DRAWER_LINK_SELECTOR = "a[data-drawer-link]";
    const DRAWER_CLOSE_SELECTOR = "[data-drawer-close]";
    const PAGE_SIZE_SELECTOR = "select[data-page-size-select]";
    const POLICY_EDITOR_SELECTOR = "#policy-editor";
    const POLICY_FORM_SELECTOR = "#policy-form";
    const POLICY_INPUT_SELECTOR = "#contentHtml";
    const QUILL_SCRIPT_URL = "https://cdn.jsdelivr.net/npm/quill@1.3.7/dist/quill.min.js";
    const QUILL_STYLE_URL = "https://cdn.jsdelivr.net/npm/quill@1.3.7/dist/quill.snow.css";

    let quillLoader = null;

    const getOffcanvasInstance = (drawerPanel) => {
        if (!(drawerPanel instanceof HTMLElement) || !window.bootstrap?.Offcanvas) {
            return null;
        }
        return window.bootstrap.Offcanvas.getOrCreateInstance(drawerPanel);
    };

    const initYouthForm = (container) => {
        const majorSelect = container.querySelector('select[name="majorCode"]');
        const majorCategoryInput = container.querySelector('input[name="majorCategory"]');
        if (!(majorSelect instanceof HTMLSelectElement) || !(majorCategoryInput instanceof HTMLInputElement)) {
            return;
        }
        if (majorSelect.dataset.initialized === "true") {
            return;
        }
        const syncMajorCategory = () => {
            const selectedOption = majorSelect.selectedOptions[0];
            majorCategoryInput.value = selectedOption ? (selectedOption.dataset.categoryValue || "") : "";
        };
        majorSelect.addEventListener("change", syncMajorCategory);
        syncMajorCategory();
        majorSelect.dataset.initialized = "true";
    };

    const ensureStylesheet = (href) => {
        if (document.querySelector(`link[href="${href}"]`)) {
            return;
        }
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = href;
        document.head.appendChild(link);
    };

    const ensureScript = (src) => {
        if (window.Quill) {
            return Promise.resolve();
        }
        if (quillLoader) {
            return quillLoader;
        }
        quillLoader = new Promise((resolve, reject) => {
            const script = document.createElement("script");
            script.src = src;
            script.onload = () => resolve();
            script.onerror = reject;
            document.body.appendChild(script);
        });
        return quillLoader;
    };

    const initPolicyEditor = async (container) => {
        const editor = container.querySelector(POLICY_EDITOR_SELECTOR);
        const form = container.querySelector(POLICY_FORM_SELECTOR);
        const hiddenInput = container.querySelector(POLICY_INPUT_SELECTOR);
        if (!editor || !form || !hiddenInput || editor.dataset.initialized === "true") {
            return;
        }

        ensureStylesheet(QUILL_STYLE_URL);
        await ensureScript(QUILL_SCRIPT_URL);

        const quill = new window.Quill(editor, {
            theme: "snow",
            placeholder: "请输入政策正文",
            modules: {
                toolbar: [
                    [{ header: [1, 2, 3, false] }],
                    ["bold", "italic", "underline"],
                    [{ list: "ordered" }, { list: "bullet" }],
                    ["link", "blockquote"],
                    ["clean"]
                ]
            }
        });
        quill.root.innerHTML = hiddenInput.value || "";
        form.addEventListener("submit", () => {
            hiddenInput.value = quill.root.innerHTML;
        });
        editor.dataset.initialized = "true";
    };

    const initDynamicDrawerContent = async (container) => {
        if (window.AlertBanner && typeof window.AlertBanner.init === "function") {
            window.AlertBanner.init(container);
        }
        if (window.BootstrapEnhancements && typeof window.BootstrapEnhancements.init === "function") {
            window.BootstrapEnhancements.init(container);
        }
        if (window.JobFormMultiSelect && typeof window.JobFormMultiSelect.init === "function") {
            window.JobFormMultiSelect.init(container);
        }
        await initPolicyEditor(container);
        initYouthForm(container);
        if (window.FormValidation && typeof window.FormValidation.init === "function") {
            window.FormValidation.init(container);
        }
        if (window.SystemRegionForm && typeof window.SystemRegionForm.init === "function") {
            window.SystemRegionForm.init(container);
        }
        if (window.RegionStepSelect && typeof window.RegionStepSelect.init === "function") {
            await window.RegionStepSelect.init(container);
        }
    };

    const setDrawerLoading = (drawerContent) => {
        drawerContent.innerHTML = `
            <div class="d-flex flex-column gap-3">
                <div>
                    <h2 class="h4 mb-1">加载中</h2>
                    <p class="text-secondary mb-0">正在加载内容...</p>
                </div>
                <div class="placeholder-glow">
                    <span class="placeholder col-12 mb-2"></span>
                    <span class="placeholder col-10 mb-2"></span>
                    <span class="placeholder col-8"></span>
                </div>
            </div>
        `;
    };

    const buildGetUrl = (form, submitter) => {
        const url = new URL(form.action || window.location.href, window.location.origin);
        const formData = submitter ? new FormData(form, submitter) : new FormData(form);
        const params = new URLSearchParams();

        for (const [key, value] of formData.entries()) {
            const normalizedValue = typeof value === "string" ? value.trim() : value;
            if (normalizedValue !== "") {
                params.append(key, normalizedValue);
            }
        }

        url.search = params.toString();
        return url.toString();
    };

    const openDrawer = async (page, url, trigger) => {
        const drawerPanel = page.querySelector(DRAWER_PANEL_SELECTOR);
        const drawerContent = page.querySelector(DRAWER_CONTENT_SELECTOR);
        const drawerTitle = page.querySelector(DRAWER_TITLE_SELECTOR);
        if (!drawerPanel || !drawerContent) {
            window.location.href = url;
            return;
        }

        page.classList.add("drawer-open");
        if (drawerTitle instanceof HTMLElement) {
            drawerTitle.textContent = trigger?.dataset.drawerTitle || trigger?.textContent?.trim() || "详细信息";
        }
        setDrawerLoading(drawerContent);
        getOffcanvasInstance(drawerPanel)?.show();

        try {
            const response = await fetch(url, {
                headers: {
                    "X-Requested-With": "XMLHttpRequest"
                }
            });
            if (response.redirected) {
                window.location.href = response.url;
                return;
            }
            if (!response.ok) {
                throw new Error(`Drawer request failed: ${response.status}`);
            }

            drawerContent.innerHTML = await response.text();
            await initDynamicDrawerContent(drawerContent);
        } catch (error) {
            console.error(error);
            window.location.href = url;
        }
    };

    const closeDrawer = (page) => {
        const drawerPanel = page.querySelector(DRAWER_PANEL_SELECTOR);
        getOffcanvasInstance(drawerPanel)?.hide();
    };

    document.addEventListener("click", (event) => {
        const link = event.target.closest(DRAWER_LINK_SELECTOR);
        if (!link) {
            return;
        }

        event.preventDefault();
        const page = link.closest(DRAWER_PAGE_SELECTOR);
        if (!page) {
            window.location.href = link.href;
            return;
        }
        openDrawer(page, link.href, link);
    });

    document.addEventListener("click", (event) => {
        const closeTrigger = event.target.closest(DRAWER_CLOSE_SELECTOR);
        if (!closeTrigger) {
            return;
        }

        const page = closeTrigger.closest(DRAWER_PAGE_SELECTOR);
        if (!page) {
            return;
        }

        event.preventDefault();
        closeDrawer(page);
    });

    document.addEventListener("hidden.bs.offcanvas", (event) => {
        const drawerPanel = event.target;
        if (!(drawerPanel instanceof HTMLElement) || !drawerPanel.matches(DRAWER_PANEL_SELECTOR)) {
            return;
        }
        const page = drawerPanel.closest(DRAWER_PAGE_SELECTOR);
        const drawerContent = drawerPanel.querySelector(DRAWER_CONTENT_SELECTOR);
        page?.classList.remove("drawer-open");
        if (drawerContent instanceof HTMLElement) {
            drawerContent.innerHTML = "";
        }
    });

    document.addEventListener("submit", async (event) => {
        const form = event.target;
        const page = form instanceof HTMLFormElement ? form.closest(DRAWER_PAGE_SELECTOR) : null;
        if (!(form instanceof HTMLFormElement) || !page) {
            return;
        }

        const drawerPanel = page.querySelector(DRAWER_PANEL_SELECTOR);
        const drawerContent = page.querySelector(DRAWER_CONTENT_SELECTOR);
        if (!drawerPanel || drawerPanel.hidden || !drawerContent || !drawerContent.contains(form)) {
            return;
        }

        event.preventDefault();
        const method = (form.method || "post").toUpperCase();
        const requestUrl = method === "GET" ? buildGetUrl(form, event.submitter) : form.action;
        const requestOptions = {
            method,
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        };
        if (method !== "GET") {
            requestOptions.body = new FormData(form);
        }

        const response = await fetch(requestUrl, requestOptions);

        if (response.redirected) {
            closeDrawer(page);
            if (window.AdminListPage) {
                await window.AdminListPage.refresh();
            } else {
                window.location.href = response.url;
            }
            return;
        }

        if (!response.ok) {
            window.location.href = form.action;
            return;
        }

        drawerContent.innerHTML = await response.text();
        await initDynamicDrawerContent(drawerContent);
    });

    document.addEventListener("change", (event) => {
        const select = event.target.closest(PAGE_SIZE_SELECTOR);
        if (!(select instanceof HTMLSelectElement)) {
            return;
        }

        const form = select.closest("form");
        const page = form instanceof HTMLFormElement ? form.closest(DRAWER_PAGE_SELECTOR) : null;
        if (!(form instanceof HTMLFormElement) || !page) {
            return;
        }

        form.requestSubmit();
    });
})();
