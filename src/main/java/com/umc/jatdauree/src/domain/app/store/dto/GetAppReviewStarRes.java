package com.umc.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppReviewStarRes { //원래에서 comment_total 추가함 그래서 못가지고 옴 GetAppReviewStarRes
    private int storeIdx;
    private double star_average;
    private int reviews_total;
    private int comment_total;
    private int star1;
    private int star2;
    private int star3;
    private int star4;
    private int star5;
}
