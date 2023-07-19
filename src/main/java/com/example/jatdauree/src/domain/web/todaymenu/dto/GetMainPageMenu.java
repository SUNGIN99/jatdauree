package com.example.jatdauree.src.domain.web.todaymenu.dto;

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
public class GetMainPageMenu implements Serializable {
    private int storeIdx;
    private List<GetMainPageItem> mainMenus;
    private List<GetMainPageItem> sideMenus;
}
