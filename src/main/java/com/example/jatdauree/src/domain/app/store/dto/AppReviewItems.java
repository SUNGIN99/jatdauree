package com.example.jatdauree.src.domain.app.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppReviewItems  {
    private int orderIdx;
    private int reviewIdx;
    private String customerName;
    private int star;
    private String contents;
    private String comment;
    private String review_url;
   private List<String> OrderTodayMenu; //AppOrderTodayMenu이게 app에서 수정된 코드이기 때문에 이 reviewDao에서 ReviewItems못가지고 옴

}
