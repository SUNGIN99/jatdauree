package com.example.jatdauree.src.domain.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTodayMenuListItem {

    private Long menuIdx;
    private String menu_name;
    private int discount;
    private int discountPrice;
    private int remain;
    private String status;
}
