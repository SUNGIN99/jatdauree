package com.example.jatdauree.src.domain.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetReviewRes implements Serializable {
    int customerIdx;
    int totalReviews;
    List<MyReviews> myReviews;
}
