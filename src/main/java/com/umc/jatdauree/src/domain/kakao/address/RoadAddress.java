package com.umc.jatdauree.src.domain.kakao.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoadAddress {
    private String address_name;
    private String building_name;
    private String main_building_no;
    private String region_1depth_name;
    private String region_2depth_name;
    private String region_3depth_name;
    private String road_name;
    private String sub_building_no;
    private String underground_yn;
    private double x;
    private double y;
    private String zone_no;
}
