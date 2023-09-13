package com.umc.jatdauree.src.domain.web.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTodayMenu implements Serializable {
    private int todayMenuIdx;
    private String meun_name;
    private int menuCount;
}
