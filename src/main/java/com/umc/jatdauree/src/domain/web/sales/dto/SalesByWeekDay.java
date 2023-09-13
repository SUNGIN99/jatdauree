package com.umc.jatdauree.src.domain.web.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesByWeekDay {
    private String date;
    private int day;
    private int totalPrice;
}
