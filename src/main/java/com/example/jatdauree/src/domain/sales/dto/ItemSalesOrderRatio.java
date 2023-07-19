package com.example.jatdauree.src.domain.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemSalesOrderRatio implements Serializable {
    private int menuIdx;
    private String menuName;
    private int menuOrderCount;
    private double  menuCharge;
}
