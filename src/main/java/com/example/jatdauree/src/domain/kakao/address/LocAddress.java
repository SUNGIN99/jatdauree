package com.example.jatdauree.src.domain.kakao.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocAddress {
    private String address_name;
    private String b_code;
    private String h_code;
    private String main_address_no;
    private String mountain_yn;
    private String region_1depth_name;
    private String region_2depth_name;
    private String region_3depth_name;
    private String region_3depth_h_name;
    private String sub_address_no;
    private double x;
    private double y;

}
