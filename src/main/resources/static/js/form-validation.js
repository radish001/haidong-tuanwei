(() => {
    const validateCustomRequired = (form) => {
        let valid = true;
        form.querySelectorAll("[data-required]").forEach((field) => {
            const value = field.value ? field.value.trim() : "";
            const isEmpty = !value || value === "<p><br></p>";
            if (isEmpty) {
                field.classList.add("is-invalid");
                valid = false;
            } else {
                field.classList.remove("is-invalid");
            }
        });
        return valid;
    };

    const initForm = (form) => {
        if (!(form instanceof HTMLFormElement) || !form.hasAttribute("novalidate")) {
            return;
        }
        if (form.dataset.validationInit === "true") {
            return;
        }

        form.addEventListener("submit", (event) => {
            const nativeValid = form.checkValidity();
            const customValid = validateCustomRequired(form);
            if (!nativeValid || !customValid) {
                event.preventDefault();
                event.stopPropagation();
                const firstInvalid = form.querySelector(":invalid, .is-invalid");
                if (firstInvalid instanceof HTMLElement) {
                    const target = firstInvalid.type === "hidden"
                        ? firstInvalid.nextElementSibling || firstInvalid.parentElement
                        : firstInvalid;
                    target.scrollIntoView({ behavior: "smooth", block: "center" });
                    if (typeof target.focus === "function" && target.type !== "hidden") {
                        target.focus();
                    }
                }
            }
            form.classList.add("was-validated");
        });

        form.addEventListener("input", (event) => {
            const field = event.target;
            if (!(field instanceof HTMLElement) || !form.classList.contains("was-validated")) {
                return;
            }
            if (field.matches(":valid")) {
                field.classList.remove("is-invalid");
            }
        });

        form.dataset.validationInit = "true";
    };

    const init = (root = document) => {
        root.querySelectorAll("form[novalidate]").forEach(initForm);
    };

    window.FormValidation = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
