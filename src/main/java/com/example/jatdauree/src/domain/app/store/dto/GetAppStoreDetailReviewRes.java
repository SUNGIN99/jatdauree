package com.example.jatdauree.src.domain.app.store.dto;
import com.example.jatdauree.src.domain.web.review.dto.*;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailReviewRes {
    private int storeIdx;
    private double starAverage;
    private int reviewsTotal;
    private int commentTotal;
    private List<StarCountRatio> starCountRatios;
    private List<AppReviewItems> reviewItems;



}
