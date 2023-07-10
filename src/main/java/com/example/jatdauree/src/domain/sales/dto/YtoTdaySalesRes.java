package com.example.jatdauree.src.domain.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YtoTdaySalesRes {
    private int storeIdx;
    private ArrayList<salesByTime> salesToday;
    private ArrayList<salesByTime> salesYesterday;
}
