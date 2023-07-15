package com.example.jatdauree.src.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMenuReq{
    @Nullable
    private ArrayList<PostMenuItem> mainMenuItems;
    @Nullable
    private ArrayList<PostMenuItem> sideMenuItems;
    @Nullable
    private ArrayList<PostIngredientItem> ingredientItems;
}