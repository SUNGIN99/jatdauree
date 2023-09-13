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
public class ItemSalesReOrNew implements Serializable {
    private int menuIdx;
    private String menuName;
    private int menuReOrderCount;
    private int menuReOrderPrice;
    private int menuNewOrderCount;
    private int menuNewOrderPrice;
    private int menuTotalSales;
}
