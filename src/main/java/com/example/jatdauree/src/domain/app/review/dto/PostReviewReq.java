package com.example.jatdauree.src.domain.app.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewReq {
    private int storeIdx;
    private int orderIdx;
    private int stars;
    private String contents;
    @Nullable private MultipartFile reviewFile; //(리뷰 파일)등록할때는 파일 형태로 등록해야 함

}
