package com.example.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailMenuItem { //가게상세-메뉴에서 메인,사이드 반환값
    private int menuIdx;
    private int todaymenuIdx;
    private String menuName;
    private String menuUrl;
    private String composition;
    private int remain;
    private int originPrice; //원가
    private int discount;
    private int todayPrice; //할인가
}
