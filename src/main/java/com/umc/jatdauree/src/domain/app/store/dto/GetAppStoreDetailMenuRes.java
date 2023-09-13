package com.umc.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailMenuRes { //가게상세-메뉴 반환값
    private int storeIdx;
    private List<GetAppStoreDetailMenuItem> mainMenuList;
    private List<GetAppStoreDetailMenuItem> sideMenuList;
}
