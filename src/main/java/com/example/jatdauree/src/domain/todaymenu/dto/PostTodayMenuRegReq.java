package com.example.jatdauree.src.domain.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTodayMenuRegReq {
    private Long storeIdx;
    private String date;
    private ArrayList<PostTodayMenuListItem> todayMenuListItems;

}
