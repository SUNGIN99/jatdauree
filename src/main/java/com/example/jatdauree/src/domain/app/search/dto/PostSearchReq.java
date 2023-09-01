package com.example.jatdauree.src.domain.app.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchReq {
    private String searchWord;
    private double longitude;
    private double latitude;
}
