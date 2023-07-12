package com.example.jatdauree.src.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewAnswerReq {

    private int storeIdx;
    private int reviewIdx;
    private String comment;
}
