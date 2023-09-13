package com.umc.jatdauree.src.domain.web.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePermitRes {
    private int storeIdx;
    private String storeName;
    private int sellerIdx;
    private String sellerPhone;
    private String sellerName;
}
