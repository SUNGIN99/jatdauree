package com.example.jatdauree.src.domain.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewReady {
    private int orderIdx;
    private int reviewIdx;
    private String storeName;
    private String menuNames;
}
