package com.example.jatdauree.src.domain.web.sales.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyMenuOrdersRes implements Serializable {
    private int storeIdx;
    private int month;
    private int totalOrderCount;
    private List<ItemSalesOrderRatio> itemOrdersRatio;
}
