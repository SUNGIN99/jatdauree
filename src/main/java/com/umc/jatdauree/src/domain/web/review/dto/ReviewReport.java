package com.umc.jatdauree.src.domain.web.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReport {
    private int reviewIdx;
    private String customerName;
    private int reviewStar;
    private String storeName;
    private String reviewContents;
    private String review_url;
}
