package com.example.jatdauree.src.domain.app.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchRes {
    private List<String> recentWords;
    private String standardTime;
    private List<String> popularWords;
}
