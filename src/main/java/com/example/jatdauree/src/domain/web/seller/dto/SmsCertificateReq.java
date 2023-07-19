package com.example.jatdauree.src.domain.web.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsCertificateReq {
    private String phoneNum;
    @Nullable private String name;
    @Nullable private String uid;
}
