(() => {
    const ROOT_SELECTOR = "[data-ajax-list-root]";
    const FORM_SELECTOR = "[data-ajax-form]";
    const AJAX_LINK_SELECTOR = "a[data-ajax-link]";
    const PAGE_SIZE_SELECTOR = "select[data-page-size-select]";
    const ROW_SELECT_SELECTOR = "input[data-row-select]";
    const SELECT_ALL_SELECTOR = "input[data-select-all]";
    const BATCH_DELETE_BUTTON_SELECTOR = "[data-batch-delete-button]";
    const CONFIRM_MODAL_ID = "global-confirm-modal";

    let activeDialogResolver = null;

    const ensureConfirmModal = () => {
        let modal = document.getElementById(CONFIRM_MODAL_ID);
        if (!modal) {
            modal = document.createElement("div");
            modal.id = CONFIRM_MODAL_ID;
            modal.className = "modal fade";
            modal.tabIndex = -1;
            modal.setAttribute("aria-hidden", "true");
            modal.innerHTML = `
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <div>
                                <h5 class="modal-title" data-dialog-title>确认操作</h5>
                                <p class="small text-secondary mb-0 mt-1" data-dialog-message></p>
                            </div>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="关闭"></button>
                        </div>
                        <div class="modal-body">
                            <p class="mb-0" data-dialog-detail hidden></p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-secondary" data-dialog-cancel>取消</button>
                            <button type="button" class="btn btn-primary" data-dialog-confirm>确认</button>
                        </div>
                    </div>
                </div>
            `;
            document.body.appendChild(modal);
        }

        const modalInstance = window.bootstrap?.Modal?.getOrCreateInstance(modal);
        if (modal.dataset.initialized !== "true") {
            modal.addEventListener("hidden.bs.modal", () => {
                if (activeDialogResolver) {
                    const result = modal.dataset.dialogResult === "true";
                    modal.dataset.dialogResult = "";
                    activeDialogResolver(result);
                    activeDialogResolver = null;
                }
            });

            modal.querySelector("[data-dialog-cancel]").addEventListener("click", () => {
                modal.dataset.dialogResult = "false";
                modalInstance?.hide();
            });

            modal.querySelector("[data-dialog-confirm]").addEventListener("click", () => {
                modal.dataset.dialogResult = "true";
                modalInstance?.hide();
            });

            modal.dataset.initialized = "true";
        }

        return modal;
    };

    const showDialog = ({ title, message, detail, confirmText = "确认", cancelText = "取消", hideCancel = false }) => {
        const modal = ensureConfirmModal();
        const modalInstance = window.bootstrap?.Modal?.getOrCreateInstance(modal);
        if (activeDialogResolver) {
            activeDialogResolver(false);
            activeDialogResolver = null;
        }

        modal.dataset.dialogResult = "false";
        modal.querySelector("[data-dialog-title]").textContent = title;
        modal.querySelector("[data-dialog-message]").textContent = message || "";
        modal.querySelector("[data-dialog-detail]").textContent = detail || "";
        modal.querySelector("[data-dialog-detail]").hidden = !detail;

        const cancelButton = modal.querySelector("[data-dialog-cancel]");
        const confirmButton = modal.querySelector("[data-dialog-confirm]");

        cancelButton.textContent = cancelText;
        cancelButton.hidden = hideCancel;
        confirmButton.textContent = confirmText;

        modalInstance?.show();
        queueMicrotask(() => confirmButton.focus());

        return new Promise((resolve) => {
            activeDialogResolver = resolve;
        });
    };

    const parseRoot = (html) => {
        const documentFragment = new DOMParser().parseFromString(html, "text/html");
        return documentFragment.querySelector(ROOT_SELECTOR) || documentFragment.body.firstElementChild;
    };

    const initDynamicListContent = async (root) => {
        if (!root) {
            return;
        }
        if (window.AlertBanner && typeof window.AlertBanner.init === "function") {
            window.AlertBanner.init(root);
        }
        if (window.BootstrapEnhancements && typeof window.BootstrapEnhancements.init === "function") {
            await window.BootstrapEnhancements.init(root);
        }
        if (window.RegionStepSelect && typeof window.RegionStepSelect.init === "function") {
            await window.RegionStepSelect.init(root);
        }
        if (window.JobFormMultiSelect && typeof window.JobFormMultiSelect.init === "function") {
            await window.JobFormMultiSelect.init(root);
        }
        if (window.YouthListPage && typeof window.YouthListPage.initMajorCascade === "function") {
            await window.YouthListPage.initMajorCascade(root);
        }
    };

    const fetchList = async (root, url, pushHistory) => {
        if (!root) {
            return;
        }

        root.classList.add("is-loading");

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
                throw new Error(`Request failed: ${response.status}`);
            }

            const html = await response.text();
            const nextRoot = parseRoot(html);
            if (!nextRoot) {
                throw new Error("List fragment not found");
            }

            root.replaceWith(nextRoot);
            await initDynamicListContent(nextRoot);
            if (pushHistory) {
                window.history.pushState({ ajaxList: true }, "", url);
            }
        } catch (error) {
            console.error(error);
            window.location.href = url;
        }
    };

    window.AdminListPage = {
        refresh(url = window.location.href) {
            const root = document.querySelector(ROOT_SELECTOR);
            if (!root) {
                return Promise.resolve();
            }
            return fetchList(root, url, false);
        }
    };

    const buildUrl = (form, submitter) => {
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

    document.addEventListener("submit", async (event) => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)) {
            return;
        }

        const submitter = event.submitter;
        const confirmMessage = form.dataset.confirmMessage;
        if (confirmMessage) {
            event.preventDefault();
            const confirmed = await showDialog({
                title: form.dataset.confirmTitle || "删除确认",
                message: confirmMessage,
                confirmText: form.dataset.confirmButtonText || "确认删除",
                cancelText: "取消"
            });
            if (!confirmed) {
                return;
            }

            if (form.matches(FORM_SELECTOR)) {
                const root = form.closest(ROOT_SELECTOR);
                fetchList(root, buildUrl(form, submitter), true);
                return;
            }

            HTMLFormElement.prototype.submit.call(form);
            return;
        }

        if (!form.matches(FORM_SELECTOR)) {
            return;
        }

        event.preventDefault();
        const root = form.closest(ROOT_SELECTOR);
        fetchList(root, buildUrl(form, submitter), true);
    });

    document.addEventListener("click", (event) => {
        const link = event.target.closest(AJAX_LINK_SELECTOR);
        if (!link) {
            return;
        }

        event.preventDefault();
        const root = link.closest(ROOT_SELECTOR);
        if (!root) {
            window.location.href = link.href;
            return;
        }
        fetchList(root, link.href, true);
    });

    document.addEventListener("change", (event) => {
        const selectAll = event.target.closest(SELECT_ALL_SELECTOR);
        if (selectAll) {
            const root = selectAll.closest(ROOT_SELECTOR) || document;
            root.querySelectorAll(ROW_SELECT_SELECTOR).forEach((checkbox) => {
                checkbox.checked = selectAll.checked;
            });
            return;
        }

        const rowCheckbox = event.target.closest(ROW_SELECT_SELECTOR);
        if (rowCheckbox) {
            const root = rowCheckbox.closest(ROOT_SELECTOR) || document;
            const selectAllCheckbox = root.querySelector(SELECT_ALL_SELECTOR);
            const rowCheckboxes = Array.from(root.querySelectorAll(ROW_SELECT_SELECTOR));
            if (selectAllCheckbox && rowCheckboxes.length > 0) {
                selectAllCheckbox.checked = rowCheckboxes.every((checkbox) => checkbox.checked);
            }
            return;
        }

        const select = event.target.closest(PAGE_SIZE_SELECTOR);
        if (!select) {
            return;
        }

        const formId = select.getAttribute("form");
        const form = formId ? document.getElementById(formId) : select.closest("form");
        if (!(form instanceof HTMLFormElement) || !form.matches(FORM_SELECTOR)) {
            return;
        }

        form.requestSubmit();
    });

    document.addEventListener("click", async (event) => {
        const button = event.target.closest(BATCH_DELETE_BUTTON_SELECTOR);
        if (!button) {
            return;
        }

        const root = button.closest(ROOT_SELECTOR) || document;
        const formId = button.dataset.batchFormId;
        const form = formId ? document.getElementById(formId) : null;
        if (!(form instanceof HTMLFormElement)) {
            return;
        }

        const selectedIds = Array.from(root.querySelectorAll(`${ROW_SELECT_SELECTOR}:checked`))
            .map((checkbox) => checkbox.value)
            .filter(Boolean);

        if (selectedIds.length === 0) {
            await showDialog({
                title: "提示",
                message: "请先勾选需要删除的数据",
                confirmText: "知道了",
                hideCancel: true
            });
            return;
        }

        form.querySelectorAll('input[name="ids"]').forEach((input) => input.remove());
        selectedIds.forEach((id) => {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = "ids";
            input.value = id;
            form.appendChild(input);
        });

        form.dataset.confirmMessage = `确认删除选中的 ${selectedIds.length} 条记录吗？`;
        form.requestSubmit();
    });

    window.addEventListener("popstate", () => {
        const root = document.querySelector(ROOT_SELECTOR);
        if (root) {
            fetchList(root, window.location.href, false);
        }
    });
})();
