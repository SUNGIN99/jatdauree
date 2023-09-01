package com.example.jatdauree.src.domain.app.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UidRecovReq {
    private String phoneNum;
    private String name;
    private String certificationNum;
}
