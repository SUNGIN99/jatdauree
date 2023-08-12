package com.example.jatdauree.src.domain.app.basket.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchBasketReq {
    private int basketIdx;
    private int inDecrease;
    private String patchStatus;
}
