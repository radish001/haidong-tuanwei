## 1. Schema And Seed Updates

- [x] 1.1 Add province/city/county code columns for youth native place, residence, and school location fields without reintroducing school-region fields on `sys_school`
- [x] 1.2 Add province/city/county code columns for enterprise region and job work region, and add `salary_range` to recruitment while retiring new writes to `salary_min` and `salary_max`
- [x] 1.3 Seed fixed dictionary items for `gender`, `experience_requirement`, and `salary_range`, and verify existing dictionary partitions still match the six-tab workbench

## 2. Backend Validation And Query Refactor

- [x] 2.1 Update youth services, DTOs, mappers, and query logic to read/write controlled select fields and three-level region codes
- [x] 2.2 Update enterprise services, DTOs, mappers, and query logic to validate controlled dictionary selections and three-level region codes
- [x] 2.3 Update recruitment services, DTOs, mappers, and query logic to use controlled enterprise/education/experience/salary selections and three-level work region codes
- [x] 2.4 Add shared region resolution and hierarchy validation so partial province-only or province-city selections are accepted while invalid combinations are rejected

## 3. Form And List Interaction Updates

- [x] 3.1 Refactor youth create/edit/list views to replace controlled text fields with selects and cascading region inputs, including independent school-location entry
- [x] 3.2 Refactor enterprise create/edit/list views to replace controlled text fields with selects and cascading region inputs
- [x] 3.3 Refactor recruitment create/edit/list views to replace salary min/max and other controlled text fields with selects and cascading region inputs
- [x] 3.4 Update shared frontend scripts or fragments needed to initialize, submit, and rehydrate three-level cascading region controls across drawers and filters

## 4. Verification

- [x] 4.1 Verify dictionary management exposes gender, experience requirement, and salary range in the intended fixed sections and blocks deletion when referenced
- [x] 4.2 Verify youth, enterprise, and recruitment forms can save with province-only, province-city, and province-city-county region selections and reject invalid hierarchy combinations
- [x] 4.3 Verify recruitment no longer depends on `salary_min` / `salary_max` for new or edited records and uses `salary_range` in form, list, and filter flows
- [x] 4.4 Run compile and focused regression checks to confirm the change is ready for `/opsx:apply`

---

## Verification Summary (2026-04-04)

**Backend:**
- ✓ Schema updated: youth_info, enterprise_info, job_post 添加三级区域码字段
- ✓ salary_range 字段替换 salary_min/salary_max
- ✓ 新增字典项：gender, experience_requirement, salary_range
- ✓ 区域层级校验 (RegionSelectionSupport)
- ✓ 受控字段校验 (validateDictValue)
- ✓ SQL歧义修复完成

**Frontend:**
- ✓ 级联区域选择组件 (region-cascader.js)
- ✓ 青年/企业/招聘表单全部使用受控下拉框
- ✓ 列表页显示完整区域名称

**Verified by API tests:**
- ✓ 青年保存：省级/省市县三级均可保存
- ✓ 企业保存：省级/省市/省市县均可保存，列表显示"青海省-海东市-乐都区"
- ✓ 招聘保存：使用 salary_range 字典，列表显示"5000-8000元"
- ✓ 非法区域拦截：省市层级不匹配时返回200表单页（不302跳转）
- ✓ 字典管理页面：gender、experience_requirement、salary_range 正确显示

**Status:** All core features implemented and verified. Application running on port 8080.
