package com.example.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchMenuUrlItem implements Serializable {
    private int menuIdx;
    private String menuName;
    private int price;
    private String composition;
    private String description;
    private String menuUrl;
}
