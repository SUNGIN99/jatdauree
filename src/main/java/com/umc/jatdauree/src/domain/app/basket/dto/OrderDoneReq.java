package com.umc.jatdauree.src.domain.app.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDoneReq {
    private int storeIdx;
    @Nullable private String request;
    private String pickupTime;
    private String paymentStatus;

}
