package com.example.jatdauree.src.domain.app.store.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppStoreDetailStatisticsInfo {
    private Integer orderCount;
    private Integer reviewCount;
    private Integer subscribeCount;
}
