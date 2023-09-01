package com.example.jatdauree.src.domain.kakao.xypoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocAddressXY {
    private String address_name;
    private String region_1depth_name;
    private String region_2depth_name;
    private String region_3depth_name;
    private String mountain_yn;
    private String main_address_no;
    private String sub_address_no;
}
