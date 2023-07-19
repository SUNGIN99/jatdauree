package com.example.jatdauree.src.domain.todaymenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMainPageTMenu implements Serializable {
    private List<GetMainPageItem> mainMenus;
    private List<GetMainPageItem> sideMenus;
}
