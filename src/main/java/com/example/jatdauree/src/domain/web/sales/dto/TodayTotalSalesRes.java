package com.example.jatdauree.src.domain.web.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodayTotalSalesRes {
    private int storeIdx;
    private String todayDate;
    private int todayTotalSales;
}
