package com.umc.jatdauree.src.domain.app.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderListRes{
    private int orderIdx;
    private String orderDate;
    private int weekDay;
    private String weekName;
    private int storeIdx;
    private String storeName;
    private String menuName;
    private String menuUrl;
    private int price;
    private int orderItemCount;
    private int reviewExist;



}
