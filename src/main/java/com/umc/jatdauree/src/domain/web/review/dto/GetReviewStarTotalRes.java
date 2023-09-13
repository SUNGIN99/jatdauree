package com.umc.jatdauree.src.domain.web.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetReviewStarTotalRes  implements Serializable {
    private int storeIdx;
    private double star_average;
    private int reviews_total;
    private List<StarCountRatio> starPoints;
}

