package com.example.jatdauree.src.domain.kakao.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Documents {
    private LocAddress address;
    private String address_name;
    private String address_type;
    private RoadAddress road_address;
    private double x;
    private double y;
}
