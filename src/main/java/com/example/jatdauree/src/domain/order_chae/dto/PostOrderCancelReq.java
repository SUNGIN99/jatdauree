package com.example.jatdauree.src.domain.order_chae.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostOrderCancelReq {
    private String uid;
    private String password;
    private int orderIdx;
    private String status;

}