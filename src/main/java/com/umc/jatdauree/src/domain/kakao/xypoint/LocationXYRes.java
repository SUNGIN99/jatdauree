package com.umc.jatdauree.src.domain.kakao.xypoint;

import com.umc.jatdauree.src.domain.kakao.Meta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationXYRes {
    private Meta meta;
    private DocumentsXY[] documents;
}
