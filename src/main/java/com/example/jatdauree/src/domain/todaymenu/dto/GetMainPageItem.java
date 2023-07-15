package com.example.jatdauree.src.domain.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMainPageItem implements Serializable {
    private int menuIdx; // menuIdx
    private String menuName; // 메뉴 원래 이름
    private int originPrice; // 할인 안된가격

    private int todaymenuIdx; //todaymenuIdx
    private int discountRatio; // 할인율
    private int discountPrice; // 할인된 가격
    private int remain; // 수량

    private String status;
    private int isUpdated; // 업데이트 여부?
}
