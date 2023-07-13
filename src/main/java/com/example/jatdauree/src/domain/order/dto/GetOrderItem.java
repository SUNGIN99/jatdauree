package com.example.jatdauree.src.domain.order.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderItem implements Serializable {
    private int orderIdx;
    private String orderTime;
    private String pickUpTime;
    @Nullable private String request;
    private int orderCount;
    private int totalPrice;
    private String payStatus;
    @Nullable  List<OrderMenu> orderItems;
}
