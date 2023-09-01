package com.example.jatdauree.src.domain.app.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppOrderTodayMenu { //원래 OrderTodayMenu에서 cnt가 빠졌기 떄문에 가져와서 수정함 AppOrderTodayMenu
    private int todayMenuIdx;
    private String menu_name;
}
//이거 List<String>으로 고정해서 이 dto 필요없을 것 같음 지워보자