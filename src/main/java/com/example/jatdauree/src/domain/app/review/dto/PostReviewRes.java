package com.example.jatdauree.src.domain.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewRes {
    private int stars;
    private String contents;
}
