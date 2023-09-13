package com.umc.jatdauree.src.domain.web.sales.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesByTime implements Serializable {
    private String time;
    private int totalSalesPriceInTime;
}
