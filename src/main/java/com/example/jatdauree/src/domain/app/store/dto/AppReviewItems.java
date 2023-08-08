package com.example.jatdauree.src.domain.app.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppReviewItems  {
    private int orderIdx;
    private int reviewIdx;
    private String customerProfileUrl;
    private String customerName;
    private int star;
    private String contents;
    private String comment;
    private String review_url;
    private List<String> OrderTodayMenu;

}
