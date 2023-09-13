package com.umc.jatdauree.src.domain.app.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PwRecovReq {
    private String phoneNum;
    private String uid;
    private String certificationNum;
}
