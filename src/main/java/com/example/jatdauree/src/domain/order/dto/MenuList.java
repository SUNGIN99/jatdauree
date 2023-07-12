package com.example.jatdauree.src.domain.order.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuList implements Serializable {
    private String menu_name;
    private String cnt;
}
