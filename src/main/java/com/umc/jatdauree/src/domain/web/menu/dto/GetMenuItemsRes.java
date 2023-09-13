package com.umc.jatdauree.src.domain.web.menu.dto;

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
public class GetMenuItemsRes implements Serializable {
    private int storeIdx;
    private List<GetMenuItem> mainMenuList;
    private List<GetMenuItem> sideMenuList;
}
