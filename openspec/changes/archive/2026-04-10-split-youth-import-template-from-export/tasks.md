## 1. Import template split

- [x] 1.1 Split the shared youth Excel header definition into separate import-template and export header definitions.
- [x] 1.2 Update youth import template generation to output the new 11-column header order: `姓名、性别、民族、出生年月、籍贯、招考年份、学历、学校、学校所在区域、专业、联系方式`.

## 2. Import parsing and validation

- [x] 2.1 Update youth import header validation to require the new import-template header set and order.
- [x] 2.2 Update youth row parsing to read columns using the new template order and stop requiring removed fields (`政治面貌`、`学位`、`毕业时间`、`就业方向`) from Excel input.
- [x] 2.3 Keep existing validation for the retained controlled fields, region paths, recruitment year, duplicate detection, and contact information.

## 3. Export stability

- [x] 3.1 Keep youth export using the current complete business field set and ensure it no longer depends on the import-template header definition.
- [x] 3.2 Verify exported column order and content remain unchanged after the import-template split.

## 4. Verification

- [x] 4.1 Update `YouthInfoServiceImplTest` to cover the new import template headers and import parsing expectations.
- [x] 4.2 Update export-related tests so they explicitly assert export still uses the current complete field set rather than the import template field set.
- [x] 4.3 Run targeted verification for youth template download, Excel import, and Excel export behavior.
## 1. Template and import header split

- [x] 1.1 Split the youth Excel import template header definition from the export header definition in `YouthInfoServiceImpl`.
- [x] 1.2 Update template generation to output the 11-column import header in the exact required order: `姓名、性别、民族、出生年月、籍贯、招考年份、学历、学校、学校所在区域、专业、联系方式`.

## 2. Import parsing and validation

- [x] 2.1 Update import header validation to accept only the new 11-column template structure.
- [x] 2.2 Update row parsing so import reads the new column positions and no longer requires 政治面貌、学位、毕业时间、就业方向 from Excel input.
- [x] 2.3 Keep recruitment year, region, school, major, ethnicity, gender, and education validation aligned with the new template fields.

## 3. Export preservation and verification

- [x] 3.1 Keep youth Excel export logic on the current full business-field set and ensure it no longer depends on the import template header array.
- [x] 3.2 Update `YouthInfoServiceImplTest` to separately verify the new import template/import behavior and the unchanged export column structure.
- [x] 3.3 Run targeted youth Excel tests or equivalent verification to confirm template download, import parsing, and export behavior all match the new contract.
