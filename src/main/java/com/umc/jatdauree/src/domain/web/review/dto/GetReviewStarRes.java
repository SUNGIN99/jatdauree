package com.umc.jatdauree.src.domain.web.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetReviewStarRes implements Serializable {
    private int storeIdx;
    private double star_average;
    private int reviews_total;
    private int star1;
    private int star2;
    private int star3;
    private int star4;
    private int star5;
}

