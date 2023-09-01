package com.example.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchMenuReq {
    @Nullable
    private ArrayList<PatchMenuItem> mainMenuItems;
    @Nullable
    private ArrayList<PatchMenuItem> sideMenuItems;
}