package com.example.jatdauree.src.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostIngredientReq {


    private long storeIdx;
    private String ingredientName;
    private String origin;
    private String menuName;
    private String status;
}
