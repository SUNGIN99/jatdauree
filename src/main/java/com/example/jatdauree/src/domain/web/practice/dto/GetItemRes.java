package com.example.jatdauree.src.domain.web.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetItemRes {
    private int itemIdx;
    private String title;
    private String contents;
}
