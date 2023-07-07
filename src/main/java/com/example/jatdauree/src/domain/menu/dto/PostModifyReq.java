package com.example.jatdauree.src.domain.menu.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostModifyReq {

    //private int sellerIdx;

    private String menuName;

    private int price;

    private String composition;

    private String description;

    private String menuUrl;

    private String status;
}
