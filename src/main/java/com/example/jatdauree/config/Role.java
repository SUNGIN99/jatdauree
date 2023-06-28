package com.example.jatdauree.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "비회원"),
    CUSTOMER("ROLE_CUSTOMER", "구매자"),
    SELLER("ROLE_SELLER", "판매자");

    private final String key;
    private final String title;
}
