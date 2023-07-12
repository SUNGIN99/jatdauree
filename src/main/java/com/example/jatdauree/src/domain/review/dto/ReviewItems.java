package com.example.jatdauree.src.domain.review.dto;

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
public class ReviewItems implements Serializable {
    private int orderIdx;
    private int reviewIdx;
    private String customerName;
    private int star;
    private String contents;
    private String review_url;
    private List<OrderTodayMenu> OrderTodayMenu;

}
