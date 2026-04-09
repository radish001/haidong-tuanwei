/**
 * 多选下拉框 - Tom Select实现
 * 替代原有的job-form-multiselect.js
 * 支持搜索、标签显示、批量选择
 */

(() => {
  const MULTISELECT_SELECTOR = "[data-multiselect]";

  /**
   * 初始化单个多选组件
   */
  const initMultiselect = (selectElement) => {
    if (!(selectElement instanceof HTMLSelectElement)) return;

    // 跳过已初始化的
    if (selectElement.tomselect) return;

    const placeholder = selectElement.dataset.placeholder || "请选择";
    const maxItems = selectElement.dataset.maxItems
      ? parseInt(selectElement.dataset.maxItems, 10)
      : null;
    const allowCreate = selectElement.dataset.allowCreate === "true";

    // 获取选项数据
    const options = Array.from(selectElement.options)
      .filter(opt => opt.value)
      .map(opt => ({
        value: opt.value,
        text: opt.textContent.trim()
      }));

    // 获取已选值
    const selectedValues = Array.from(selectElement.selectedOptions).map(opt => opt.value);

    // 配置Tom Select
    const config = {
      plugins: ["remove_button", "dropdown_input"],
      placeholder: placeholder,
      valueField: "value",
      labelField: "text",
      searchField: "text",
      maxItems: maxItems,
      options: options,
      items: selectedValues,
      hideSelected: true,
      closeAfterSelect: false,
      // 保持与原始select的同步
      onChange: function(values) {
        // 同步原始select的选中状态
        Array.from(selectElement.options).forEach(opt => {
          opt.selected = values.includes(opt.value);
        });
        // 触发change事件
        selectElement.dispatchEvent(new Event("change", { bubbles: true }));
      }
    };

    // 允许创建新选项（如果有配置）
    if (allowCreate) {
      config.plugins.push("create");
      config.create = true;
      config.createOnBlur = true;
      config.addPrecedence = true;
    }

    // 初始化
    const tom = new TomSelect(selectElement, config);

    // 添加自定义样式类
    tom.wrapper.classList.add("tomselect-multiple");

    return tom;
  };

  /**
   * 从服务器加载选项的多选组件
   */
  const initAjaxMultiselect = async (selectElement) => {
    if (!(selectElement instanceof HTMLSelectElement)) return;

    const apiUrl = selectElement.dataset.apiUrl;
    if (!apiUrl) {
      // 没有API URL，按普通多选初始化
      return initMultiselect(selectElement);
    }

    const placeholder = selectElement.dataset.placeholder || "请选择";
    const maxItems = selectElement.dataset.maxItems
      ? parseInt(selectElement.dataset.maxItems, 10)
      : null;

    try {
      const response = await fetch(apiUrl, {
        headers: { "X-Requested-With": "XMLHttpRequest" }
      });

      if (!response.ok) {
        throw new Error(`Failed to load options: ${response.status}`);
      }

      const data = await response.json();

      // 清空原有选项
      selectElement.innerHTML = "";

      // 添加新选项
      data.forEach(item => {
        const option = document.createElement("option");
        option.value = item.value || item.dictValue || item.id;
        option.textContent = item.text || item.dictLabel || item.name;
        if (item.selected) {
          option.selected = true;
        }
        selectElement.appendChild(option);
      });

      // 初始化多选
      return initMultiselect(selectElement);

    } catch (error) {
      console.error("[MultiSelect] Failed to load options:", error);
      // 失败时仍尝试初始化现有选项
      return initMultiselect(selectElement);
    }
  };

  /**
   * 初始化所有多选组件
   */
  const init = async (root = document) => {
    const selects = Array.from(root.querySelectorAll(MULTISELECT_SELECTOR));

    for (const select of selects) {
      try {
        if (select.dataset.apiUrl) {
          await initAjaxMultiselect(select);
        } else {
          initMultiselect(select);
        }
      } catch (error) {
        console.error("[MultiSelect] Failed to initialize:", error);
      }
    }
  };

  /**
   * 动态更新选项
   */
  const updateOptions = (selectElement, newOptions, selectedValues = []) => {
    if (!selectElement.tomselect) return;

    const tom = selectElement.tomselect;

    // 清空当前选项和值
    tom.clear();
    tom.clearOptions();

    // 添加新选项
    tom.addOptions(newOptions.map(opt => ({
      value: opt.value,
      text: opt.text
    })));

    // 设置选中值
    if (selectedValues.length > 0) {
      tom.setValue(selectedValues);
    }
  };

  /**
   * 获取选中值
   */
  const getValue = (selectElement) => {
    if (selectElement.tomselect) {
      return selectElement.tomselect.getValue();
    }
    return Array.from(selectElement.selectedOptions).map(opt => opt.value);
  };

  /**
   * 设置选中值
   */
  const setValue = (selectElement, values) => {
    if (selectElement.tomselect) {
      selectElement.tomselect.setValue(values);
    } else {
      // 回退到原生select
      Array.from(selectElement.options).forEach(opt => {
        opt.selected = values.includes(opt.value);
      });
    }
  };

  /**
   * 清空选择
   */
  const clear = (selectElement) => {
    if (selectElement.tomselect) {
      selectElement.tomselect.clear();
    } else {
      selectElement.selectedIndex = -1;
    }
  };

  /**
   * 销毁组件
   */
  const destroy = (selectElement) => {
    if (selectElement.tomselect) {
      selectElement.tomselect.destroy();
    }
  };

  // 全局API
  window.MultiSelectTomSelect = {
    init,
    initMultiselect,
    initAjaxMultiselect,
    updateOptions,
    getValue,
    setValue,
    clear,
    destroy
  };

  // 自动初始化
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () => init());
  } else {
    init();
  }

  // 监听抽屉打开事件
  document.addEventListener("drawer:opened", (event) => {
    if (event.detail && event.detail.container) {
      init(event.detail.container);
    }
  });
})();
