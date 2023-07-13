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
public class GetOrderProRes implements Serializable {
    private int storeIdx;
    List<GetOrderItem> orderLists;
}
