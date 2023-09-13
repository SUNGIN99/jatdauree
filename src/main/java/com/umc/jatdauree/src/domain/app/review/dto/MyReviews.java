package com.umc.jatdauree.src.domain.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyReviews implements Serializable {
    private int storeIdx;
    private String storeName;
    private int reviewIdx;
    private int star;
    private String date;
    @Nullable private String reviewUrl;
    private String contents;
    @Nullable private String sellerComment;
    private List<String> reviewMenus;

}
