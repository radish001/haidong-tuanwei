package com.haidong.tuanwei.analytics.dto;

import com.haidong.tuanwei.analytics.entity.ChartItem;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagChartView {

    private String title;
    private List<ChartItem> data;
}
