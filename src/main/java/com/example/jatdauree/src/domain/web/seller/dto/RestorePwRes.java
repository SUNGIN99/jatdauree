package com.example.jatdauree.src.domain.web.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestorePwRes {
    int sellerIdx;
    int pwRestoreComplete;
}
