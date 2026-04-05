## 1. Data Models And Persistence

- [x] 1.1 Extend the existing dictionary model so fixed business dictionary types can be queried and maintained without supporting dynamic top-level category creation
- [x] 1.2 Add persistent models and schema changes for professional names, schools, school tags, and their association relationships
- [x] 1.3 Add repository and service support for association-aware delete checks across public dictionaries, school data, enterprise dictionaries, and hierarchical regions

## 2. System Dictionary Workbench

- [x] 2.1 Refactor the dictionary management page into a fixed six-tab workbench with unified query area, action area, pagination, and Ajax refresh behavior
- [x] 2.2 Implement CRUD flows for the fixed public dictionary types, major categories, enterprise dictionaries, and school-related dictionary sections using drawer-based forms
- [x] 2.3 Add province, city, and district/county maintenance with cascading region browsing, create/edit flows, and parent-child validation
- [x] 2.4 Add delete confirmation and failure feedback so blocked deletions clearly explain which business data or child regions are still referencing the selected item

## 3. Master Data Integration

- [x] 3.1 Implement professional name management so each professional name must bind to one professional category
- [x] 3.2 Implement school management so each school must bind to one school category and can bind multiple school tags
- [x] 3.3 Integrate enterprise size, enterprise nature, and enterprise industry dictionaries into enterprise create/edit and query flows
- [x] 3.4 Integrate controlled education, major, and school sources into youth-related validation or form flows needed by this change

## 4. Validation And Verification

- [x] 4.1 Verify delete guards for school categories, school tags, schools, enterprise dictionaries, public dictionaries, and hierarchical regions against real association scenarios
- [x] 4.2 Verify the six-tab dictionary workbench keeps consistent layout and interaction behavior with the existing admin list-page standard
- [x] 4.3 Verify province, city, and district/county management supports expected cascade selection and blocked deletion behavior
- [x] 4.4 Run compile and focused end-to-end checks to confirm the new master data management flows are apply-ready
