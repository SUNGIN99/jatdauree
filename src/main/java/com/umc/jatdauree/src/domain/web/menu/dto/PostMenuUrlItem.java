package com.umc.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMenuUrlItem implements Serializable {
    private String menuName;
    private int price;
    private String composition;
    private String description;
    private String menuUrl;
}
