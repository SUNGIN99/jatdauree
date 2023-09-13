package com.umc.jatdauree.src.domain.app.subscribe.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppSubscriptionRes {
    private int subIdx;
    private String status;
}
