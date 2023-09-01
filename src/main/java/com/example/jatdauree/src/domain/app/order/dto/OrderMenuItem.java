package com.example.jatdauree.src.domain.app.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderMenuItem {
    private String menuName;
    private int menuCount;
    private int originPrice;
    private int discount;
    private int discountedPrice;
}
