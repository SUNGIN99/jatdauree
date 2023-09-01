package com.example.jatdauree.src.domain.web.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderMenuCntPrirce implements Serializable {
    private String menuName;
    private int menuCnt;
    private int discountedPrice;
}
