(() => {
    const DRAWER_PAGE_SELECTOR = "[data-drawer-page]";
    const DRAWER_PANEL_SELECTOR = "[data-drawer-panel]";
    const DRAWER_CONTENT_SELECTOR = "[data-drawer-content]";
    const DRAWER_LINK_SELECTOR = "a[data-drawer-link]";
    const DRAWER_CLOSE_SELECTOR = "[data-drawer-close]";
    const POLICY_EDITOR_SELECTOR = "#policy-editor";
    const POLICY_FORM_SELECTOR = "#policy-form";
    const POLICY_INPUT_SELECTOR = "#contentHtml";
    const QUILL_SCRIPT_URL = "https://cdn.jsdelivr.net/npm/quill@1.3.7/dist/quill.min.js";
    const QUILL_STYLE_URL = "https://cdn.jsdelivr.net/npm/quill@1.3.7/dist/quill.snow.css";

    let quillLoader = null;

    const initYouthForm = (container) => {
        const majorSelect = container.querySelector('select[name="major"]');
        const majorCategoryInput = container.querySelector('input[name="majorCategory"]');
        if (!(majorSelect instanceof HTMLSelectElement) || !(majorCategoryInput instanceof HTMLInputElement)) {
            return;
        }
        if (majorSelect.dataset.initialized === "true") {
            return;
        }
        const syncMajorCategory = () => {
            const selectedOption = majorSelect.selectedOptions[0];
            majorCategoryInput.value = selectedOption ? (selectedOption.dataset.categoryLabel || "") : "";
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
        await initPolicyEditor(container);
        initYouthForm(container);
        if (window.SystemRegionForm && typeof window.SystemRegionForm.init === "function") {
            window.SystemRegionForm.init(container);
        }
        // 旧的三级级联组件（保留兼容）
        if (window.RegionCascader && typeof window.RegionCascader.init === "function") {
            await window.RegionCascader.init(container);
        }
        // 箭头展开式级联选择器
        if (window.RegionStepSelect && typeof window.RegionStepSelect.init === "function") {
            await window.RegionStepSelect.init(container);
        }
    };

    const setDrawerLoading = (drawerContent) => {
        drawerContent.innerHTML = `
            <section class="drawer-panel record-drawer-inner">
                <div class="panel-header">
                    <div>
                        <h2>加载中</h2>
                        <p>正在打开右侧详情面板...</p>
                    </div>
                </div>
            </section>
        `;
    };

    const openDrawer = async (page, url) => {
        const drawerPanel = page.querySelector(DRAWER_PANEL_SELECTOR);
        const drawerContent = page.querySelector(DRAWER_CONTENT_SELECTOR);
        if (!drawerPanel || !drawerContent) {
            window.location.href = url;
            return;
        }

        page.classList.add("drawer-open");
        drawerPanel.hidden = false;
        setDrawerLoading(drawerContent);

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
        const drawerContent = page.querySelector(DRAWER_CONTENT_SELECTOR);
        page.classList.remove("drawer-open");
        if (drawerContent) {
            drawerContent.innerHTML = "";
        }
        if (drawerPanel) {
            drawerPanel.hidden = true;
        }
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
        openDrawer(page, link.href);
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
        const response = await fetch(form.action, {
            method: (form.method || "post").toUpperCase(),
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            },
            body: new FormData(form)
        });

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
})();
