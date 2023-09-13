package com.umc.jatdauree.src.domain.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    private String is_end;
    private int pageable_count;
    private int total_count;
}
