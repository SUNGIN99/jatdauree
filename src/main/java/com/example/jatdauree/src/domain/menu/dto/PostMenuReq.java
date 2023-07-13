package com.example.jatdauree.src.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMenuReq implements Serializable {
    private ArrayList<PostMenuItem> postMenuItems;
    @Nullable private ArrayList<PostMenuItem> postSideMenuItems;
    private ArrayList<PostIngredientItem> postIngredientItems;
}