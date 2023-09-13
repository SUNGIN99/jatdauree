package com.umc.jatdauree.src.domain.app.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePoint {
    private String storeName;
    private double longitude;
    private double latitude;
}
