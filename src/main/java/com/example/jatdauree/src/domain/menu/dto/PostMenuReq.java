package com.example.jatdauree.src.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMenuReq {

    private long storeIdx;

    private String menuName;

    private int price;

    private String composition;

    private String description;

    private String menuUrl;

    private String status;


}
//PostMenuReq DTO 클래스의 필드명과 MenuDao의 SQL 쿼리에서 사용하는 컬럼명이 일치시켜야 함
//menu_name,price,composition,description,menu_url