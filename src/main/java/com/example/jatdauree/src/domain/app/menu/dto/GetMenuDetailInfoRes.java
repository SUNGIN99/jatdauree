package com.example.jatdauree.src.domain.app.menu.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMenuDetailInfoRes {
    private int storeIdx;
    private int todaymenuIdx;
    private String menuUrl;
    private String menuName;
    private int remain;
    private String composition;
    private String description;
    private int price;
    private int discount;
    private int todayPrice;
}
