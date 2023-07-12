package com.example.jatdauree.src.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderRes {

    private int orderIdx;
    private String store_name;
    private String uid;
    private String order_time;
    private int total_menu;
    private int total_price;
    private String menu_name;
    private String cnt;
    private String pickup_time;
    private String request;
    private String payment_status;
    private String status;
    //ArrayList<MenuList> menuLists;


}
