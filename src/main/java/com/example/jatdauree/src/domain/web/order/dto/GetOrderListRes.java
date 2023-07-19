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
public class GetOrderListRes <T> implements Serializable {
    private int storeIdx;
    private List<T> orderLists;
}
