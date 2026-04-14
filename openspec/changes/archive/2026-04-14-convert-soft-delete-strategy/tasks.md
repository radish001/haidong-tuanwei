## 1. Schema And Data Semantics

- [x] 1.1 Update `src/main/resources/schema.sql` to keep soft delete only on `youth_info`, `enterprise_info`, `job_post`, and `policy_article`
- [x] 1.2 Remove `deleted` columns and deleted-related indexes from non-business tables that will use physical delete
- [x] 1.3 Review unique keys and relation-table definitions so recreated master data and rebuilt relations no longer conflict with historical soft-deleted rows

## 2. Delete Flow Refactor

- [x] 2.1 Refactor system/master-data mappers and services to replace soft-delete updates with physical delete operations
- [x] 2.2 Refactor auth and permission configuration mappers and services to use physical deletion for users, roles, menus, and relation tables
- [x] 2.3 Keep `youth_info`, `enterprise_info`, `job_post`, and `policy_article` on soft delete and preserve their default `deleted = 0` query filters
- [x] 2.4 Ensure `job_post` deletion still cleans up its physical relation tables consistently

## 3. Query And Dependency Cleanup

- [x] 3.1 Remove `deleted = 0` conditions from queries and joins that target tables converted to physical delete
- [x] 3.2 Keep or adjust reference-check queries so master/config deletes still fail when active business usage exists
- [x] 3.3 Simplify analytics and option-loading queries to align with the mixed strategy boundary after the refactor

## 4. Test And Regression Coverage

- [x] 4.1 Update integration and unit tests that currently assume soft delete on master/config or relation tables
- [x] 4.2 Convert current bug-characterization tests for deleted master data and relations into passing regression tests for the new physical-delete behavior
- [x] 4.3 Run the full Maven test suite and fix any regressions caused by the delete-strategy migration
