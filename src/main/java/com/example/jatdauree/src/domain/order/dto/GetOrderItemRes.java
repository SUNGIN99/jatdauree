package com.example.jatdauree.src.domain.order.dto;

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
public class GetOrderItemRes implements Serializable {

    private int orderIdx;
    private String orderTime;
    private String pickUpTime;
    private String request;
    private int totalPrice;
    private String payStatus;
    private String menuName;
    private int menuCount;
    private int discountedPrice;
}
