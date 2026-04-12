## 1. Major category data contract

- [x] 1.1 Extend major master-data models, DAO mappings, and query results to expose major-category `dictValue` alongside existing category labels.
- [x] 1.2 Keep the existing major-by-category API flow working with category `dictValue`, and verify the returned payload is sufficient for youth-page cascade selection.

## 2. Youth persistence and query alignment

- [x] 2.1 Update youth create, edit, and import mapping so `majorCategory` is stored as the selected major's category `dictValue` instead of the display label.
- [x] 2.2 Update youth query and display mappings so list, detail, and form flows resolve stored `majorCategory` values back to readable category labels.
- [x] 2.3 Verify youth major-category filtering uses the unified stored `dictValue` contract without relying on category-label persistence.

## 3. Youth page cascade interaction

- [x] 3.1 Update the youth filter form so selecting a major category reloads the major options and preserves the current major only when it still belongs to that category.
- [x] 3.2 Update the youth edit form so selecting a major synchronizes the hidden `majorCategory` field with the major-category `dictValue`.
- [ ] 3.3 Validate the end-to-end behavior for list filtering, create/edit submission, and import-created youth records under the new category-value contract.
