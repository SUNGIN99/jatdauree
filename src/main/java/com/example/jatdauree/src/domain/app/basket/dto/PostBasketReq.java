package com.example.jatdauree.src.domain.app.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostBasketReq {
    private int storeIdx;
    private int todaymenuIdx;
    private int count;
    private int sameStoreCheck;
    // 0 이면 그냥 삽입 , 1이면 현재 장바구니 초기화 후 삽입

}
