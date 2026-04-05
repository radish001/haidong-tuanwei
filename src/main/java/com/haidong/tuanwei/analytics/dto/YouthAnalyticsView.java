package com.haidong.tuanwei.analytics.dto;

import com.haidong.tuanwei.analytics.entity.ChartItem;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YouthAnalyticsView {

    private List<ChartItem> ageDistribution;
    private List<ChartItem> genderDistribution;
    private List<ChartItem> educationDistribution;
    private List<ChartItem> ethnicityDistribution;
    private List<ChartItem> politicalStatusDistribution;
    private List<ChartItem> entrepreneurshipDemandDistribution;
}
