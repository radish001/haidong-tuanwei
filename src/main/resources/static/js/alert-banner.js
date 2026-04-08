(() => {
    const ALERT_SELECTOR = ".alert";
    const MESSAGE_CLASS = "alert-message";
    const CLOSE_SELECTOR = "[data-alert-close]";

    const enhanceAlert = (alert) => {
        if (!(alert instanceof HTMLElement) || alert.querySelector(CLOSE_SELECTOR)) {
            return;
        }

        const message = document.createElement("span");
        message.className = MESSAGE_CLASS;
        message.textContent = alert.textContent.trim();
        alert.textContent = "";
        alert.appendChild(message);

        const closeButton = document.createElement("button");
        closeButton.type = "button";
        closeButton.className = "alert-close";
        closeButton.setAttribute("data-alert-close", "");
        closeButton.setAttribute("aria-label", "关闭");
        closeButton.textContent = "×";
        closeButton.addEventListener("click", () => {
            alert.remove();
        });
        alert.appendChild(closeButton);
    };

    const init = (root = document) => {
        root.querySelectorAll(ALERT_SELECTOR).forEach(enhanceAlert);
    };

    window.AlertBanner = { init };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => init());
    } else {
        init();
    }
})();
