package com.example.jatdauree.src.domain.web.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YtoTdaySalesRes implements Serializable {
    private int storeIdx;
    private String storeOpen;
    private String storeClose;
    private List<SalesByTime> salesToday;
    private List<SalesByTime> salesYesterday;
}
