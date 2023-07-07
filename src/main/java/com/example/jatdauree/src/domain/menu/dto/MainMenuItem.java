package com.example.jatdauree.src.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MainMenuItem implements Serializable {
    private String menuName;
    private int price;
    private String composition;
    private String description;
    private String menuUrl;
}
