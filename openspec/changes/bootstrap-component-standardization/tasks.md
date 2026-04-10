## 1. Shared Bootstrap Shell

- [x] 1.1 Refactor `fragments/sidebar.html` and `fragments/topbar.html` to rely on Bootstrap navigation, dropdown, breadcrumb, and utility classes as the primary structure
- [x] 1.2 Replace the custom drawer container pattern with a shared Bootstrap `offcanvas` container and update `record-drawer.js` to load AJAX content into the Bootstrap drawer body
- [x] 1.3 Replace the custom confirm dialog implementation in `list-pagination.js` with a shared Bootstrap `modal` flow

## 2. Page Template Convergence

- [x] 2.1 Update list pages such as `youth/list.html`, `job/list.html`, `enterprise/list.html`, `policy/list.html`, and `system/dictionaries.html` to remove wrappers that only exist for the legacy custom shell
- [x] 2.2 Update drawer-loaded templates such as `youth/form.html`, `youth/detail.html`, `job/form.html`, `job/match-results.html`, `enterprise/form.html`, `policy/form.html`, `policy/detail.html`, and `system/*-form.html` to fit the Bootstrap `offcanvas` structure
- [x] 2.3 Normalize dashboard, analytics, login, and system pages to use Bootstrap cards, headings, alerts, tables, and spacing utilities as the default structural layer

## 3. CSS and Legacy Asset Cleanup

- [x] 3.1 Reduce `src/main/resources/static/css/app.css` to Bootstrap theme overrides, Tom Select overrides, TreeselectJS overrides, and necessary brand visual tokens only
- [x] 3.2 Remove legacy CSS blocks and compatibility hooks for superseded custom components such as custom drawers, custom confirm modals, old multiselect UI, and old region panel UI
- [x] 3.3 Remove obsolete scripts, branches, and template hooks that are no longer needed once Bootstrap and the selected plugins are the only active implementations

## 4. Verification

- [ ] 4.1 Verify drawer, modal, pagination, and enhanced select behavior still works after the Bootstrap conversion on representative list and form pages
- [x] 4.2 Verify the final `app.css` no longer contains general-purpose structural implementations for drawers, modals, pagination, multiselect, or region cascaders
- [ ] 4.3 Run targeted regression checks across dashboard, youth, job, enterprise, policy, system, analytics, and login pages to confirm consistent dark-theme presentation and no references to removed legacy assets remain
