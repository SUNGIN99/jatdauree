package com.example.jatdauree.src.domain.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedNumConfReq {

    private String phoneNum;
    @Nullable private String name;
    @Nullable private String uid;
    private String certificationNum;
}
