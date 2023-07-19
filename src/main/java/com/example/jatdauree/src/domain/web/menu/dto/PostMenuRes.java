package com.example.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMenuRes {
    private int storeIdx;
    private int mainMenuItemCount;
    private int sideMenuItemCount;
    private int ingredientCount;
}
