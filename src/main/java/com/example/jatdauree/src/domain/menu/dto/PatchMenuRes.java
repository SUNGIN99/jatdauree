package com.example.jatdauree.src.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchMenuRes {
    private int storeIdx;
    private int totalItemCount;
    private int newMainItemCount;
    private int updMainItemCount;
    private int newSideItemCount;
    private int updSideItemCount;
}
