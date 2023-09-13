package com.umc.jatdauree.src.domain.web.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMainPageTMenuRes {
    private int storeIdx;
    private int newTodayMainCount;
    private int updateTodayMainCount;
    private int newTodaySideCount;
    private int updateTodaySideCount;

}
