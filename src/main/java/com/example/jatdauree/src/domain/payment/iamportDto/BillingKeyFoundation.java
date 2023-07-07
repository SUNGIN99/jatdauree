package com.example.jatdauree.src.domain.payment.iamportDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillingKeyFoundation {
    private String customer_uid;
    private String card_name;
    private String card_nickName;
    private String pw6;
}
