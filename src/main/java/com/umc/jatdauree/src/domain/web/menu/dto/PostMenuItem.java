package com.umc.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMenuItem implements Serializable {
    private String menuName;
    private int price;
    private String composition;
    private String description;
    private MultipartFile menuUrl;
}
