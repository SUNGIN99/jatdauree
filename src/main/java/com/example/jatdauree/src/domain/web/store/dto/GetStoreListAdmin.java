package com.example.jatdauree.src.domain.web.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreListAdmin {
    private int storeIdx;
    private String storeNames;
    private String storeRegisterDate;
}
