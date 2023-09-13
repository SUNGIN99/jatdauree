package com.umc.jatdauree.src.domain.web.store.dto;

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
    private String storeName;
    private String storeRegisterDate;
}
