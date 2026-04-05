## 1. Shared Layout And Navigation

- [x] 1.1 Audit youth, enterprise, recruitment, and policy list templates to identify the shared query area, action area, pagination, and drawer placeholders that must be standardized
- [x] 1.2 Refactor shared CSS and common scripts so all target list pages use the same top search layout, right-aligned operation area, drawer transition, and confirmation modal behavior
- [x] 1.3 Update sidebar menu rendering and seed data so enterprise and recruitment appear under a unified `企业招聘信息` entry and `政策管理` is renamed to `就业创业政策`

## 2. List Page Consistency

- [x] 2.1 Update the enterprise list page to match the youth page structure, including query area, operation buttons under the query area, pagination controls, and deletion confirmation behavior
- [x] 2.2 Update the recruitment list page to match the youth page structure, including query area, operation buttons under the query area, pagination controls, and deletion confirmation behavior
- [x] 2.3 Update the policy list page to match the youth page structure, including query area, operation buttons under the query area, pagination controls, and deletion confirmation behavior
- [x] 2.4 Verify the youth list page remains the layout baseline and still supports batch delete, pagination, confirmation modal, and partial refresh after the shared refactor

## 3. Drawer-Based Detail And Edit Flows

- [x] 3.1 Add controller support for full-page and Ajax fragment rendering for enterprise create/edit flows and wire the enterprise list to open those forms in a right-side drawer
- [x] 3.2 Add controller support for full-page and Ajax fragment rendering for recruitment create/edit flows and wire the recruitment list to open those forms in a right-side drawer
- [x] 3.3 Add controller support for full-page and Ajax fragment rendering for policy create/detail/edit flows and wire the policy list to open them in a right-side drawer
- [x] 3.4 Add controller support for full-page and Ajax fragment rendering for youth create/detail/edit flows and ensure drawer submission closes the drawer and refreshes the list

## 4. Verification

- [x] 4.1 Verify each target page supports query, pagination, page-size selection, delete confirmation, and drawer open/close without full-page refresh
- [x] 4.2 Run compile and targeted manual checks for enterprise, recruitment, policy, and youth pages to confirm layout and interaction consistency with the youth page standard
