package com.umc.jatdauree.src.domain.app.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRes {
    private String orderDate;
    private int storeIdx;
    private String storeName;
    private String storePhone;
    private String simpleMenu;
    private List<OrderMenuItem> orderMenus;
    private int count;
    private int totalPrice;
    private String payStatus;
    private String request;
}
