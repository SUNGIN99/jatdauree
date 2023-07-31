package com.example.jatdauree.src.domain.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationInfoRes {
    private Documents[] documents;
    private Meta meta;
}
