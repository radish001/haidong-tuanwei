## 1. Analytics Data Aggregation

- [x] 1.1 Extend analytics DAO queries to aggregate school category, major category, and Haidong-native school-tag statistics for college youth
- [x] 1.2 Update analytics service and DTO outputs to expose the five fixed chart datasets and the dynamic school-tag chart datasets
- [x] 1.3 Implement Haidong-native filtering logic based on native region codes and keep tag-based analysis non-mutually-exclusive

## 2. Dashboard Rendering

- [x] 2.1 Refactor the college analytics page layout into a fixed basic-profile section plus a dynamic school-tag专题 section
- [x] 2.2 Replace chart configurations so school category uses a donut chart, major category uses a vertical bar chart, ethnicity uses a pie chart, education uses a horizontal bar chart, and gender uses a mirrored comparison bar chart
- [x] 2.3 Render one donut chart per school tag using the tag name as the chart title and support responsive wrapping for the tag chart area

## 3. Verification

- [x] 3.1 Verify the college analytics page shows the five fixed charts with the agreed chart types and data groupings
- [x] 3.2 Verify school category analysis matches school category dictionary values and tag charts are generated dynamically from existing school tags
- [x] 3.3 Verify Haidong-native filtering uses native region codes, and document that tag charts are independent hits rather than mutually exclusive totals
