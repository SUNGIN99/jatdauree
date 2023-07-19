package com.example.jatdauree.src.domain.web.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YtoTWeekSalesRes {
    private int storeIdx;
    private List<SalesByWeekDay> thisWeek;
    private List<SalesByWeekDay> lastWeek;
}
