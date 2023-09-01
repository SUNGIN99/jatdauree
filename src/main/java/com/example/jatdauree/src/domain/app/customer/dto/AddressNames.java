package com.example.jatdauree.src.domain.app.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressNames {
    private String locAddress; // 지번
    private String roadAddress; // 도로명
}
