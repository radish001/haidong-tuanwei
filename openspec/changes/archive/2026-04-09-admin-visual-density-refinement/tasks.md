## 1. Shared Visual Rules

- [x] 1.1 Rework shared spacing, radius, border, shadow, and typography tokens in `src/main/resources/static/css/app.css` to reduce visual noise and improve page density
- [x] 1.2 Refine `fragments/sidebar.html` and `fragments/topbar.html` with matching Bootstrap utilities so the shared shell feels lighter, tighter, and better aligned on desktop and mobile

## 2. List Workbench Layout

- [x] 2.1 Update representative list pages such as `youth/list.html`, `job/list.html`, `enterprise/list.html`, `policy/list.html`, and `system/dictionaries.html` to remove redundant page title blocks and converge on a compact “tabs + toolbar + table + pagination” structure
- [x] 2.2 Normalize list-page filters, action buttons, table headers, row actions, and pagination controls so they follow the same alignment, density, and responsive wrapping rules across modules

## 3. Dashboard And Analytics Grid

- [x] 3.1 Refine `dashboard/index.html` to reduce repeated title regions and organize maps, stats, and summary lists into a cleaner Bootstrap grid hierarchy
- [x] 3.2 Refine `analytics/index.html` to use a more regular chart grid with lighter section framing, reduced decorative card nesting, and consistent chart spacing at large and medium breakpoints

## 4. Offcanvas Workspace Refinement

- [x] 4.1 Update drawer-loaded templates such as `youth/form.html`, `youth/detail.html`, `job/form.html`, `job/match-results.html`, `enterprise/form.html`, `policy/form.html`, `policy/detail.html`, and `system/*-form.html` to behave like compact right-side work panels instead of full secondary pages
- [x] 4.2 Adjust shared offcanvas-related styling and layout hooks so headers, body content, and footer actions align consistently and remain usable on smaller screens

## 5. Verification

- [x] 5.1 Verify representative list pages, dashboard, analytics, login, and drawer forms still present a consistent dark-theme government-dashboard style across major breakpoints
- [x] 5.2 Verify the final implementation does not reintroduce redundant title cards, oversized whitespace, or non-Bootstrap structural wrappers into shared templates and theme styles
