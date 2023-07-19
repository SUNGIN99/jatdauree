package com.example.jatdauree.src.domain.web.payment.dto;

import com.google.gson.annotations.SerializedName;
import com.siot.IamportRestClient.request.BillingCustomerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssueBillingReq {
    private String cardNickName;
    private String pw6;
    private String card_number;
    private String expiry;
    private String birth;
    private String pwd_2digit;
}
