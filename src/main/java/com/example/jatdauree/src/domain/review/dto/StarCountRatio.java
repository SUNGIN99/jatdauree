package com.example.jatdauree.src.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StarCountRatio implements Serializable {
    private String starPoint;
    private int starCount;
    private int starRatio;
}
