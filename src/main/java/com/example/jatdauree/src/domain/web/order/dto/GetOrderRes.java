package com.example.jatdauree.src.domain.web.order.dto;

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
public class GetOrderRes implements Serializable {

    private int orderIdx;
    private int orderSequence;
    private String orderTime;
    private String pickUpTime;
    private String request;
    private int menuDiverse;
    private int totalPrice;
    private String payStatus;
    private List<OrderMenuCntPrirce> orderItem;
}
