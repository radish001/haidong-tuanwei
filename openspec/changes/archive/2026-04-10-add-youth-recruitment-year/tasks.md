## 1. Database and data model

- [x] 1.1 Add `recruitment_year` to `youth_info` in `schema.sql`, including incremental alter-table compatibility logic.
- [x] 1.2 Add the same `recruitment_year` column to `src/test/resources/schema-h2.sql` and update seeded `youth_info` records in `data.sql` as needed.
- [x] 1.3 Extend `YouthInfo` and `YouthFormRequest` to carry the recruitment year field through the backend model.

## 2. Persistence and business logic

- [x] 2.1 Update `YouthInfoDao.xml` result mapping, select columns, insert SQL, and update SQL to read and write recruitment year.
- [x] 2.2 Update `YouthController.toForm()` and `YouthInfoServiceImpl.toEntity()` so recruitment year is persisted and correctly echoed in edit flows.
- [x] 2.3 Add backend validation for recruitment year as a 4-digit year value while keeping the field nullable for existing data.

## 3. UI and Excel workflows

- [x] 3.1 Add recruitment year input and echo support in `youth/form.html` and `youth/detail.html` without widening the current youth list layout.
- [x] 3.2 Update the youth Excel template headers and export column order to include the recruitment year field.
- [x] 3.3 Update Excel import parsing and validation to accept valid recruitment year values and reject malformed ones with clear row-level errors.

## 4. Verification

- [x] 4.1 Update `YouthInfoServiceImplTest` for the new template, import, and export column expectations.
- [x] 4.2 Run targeted tests or verification covering youth create/edit, detail echo, and Excel import/export behavior with recruitment year.
