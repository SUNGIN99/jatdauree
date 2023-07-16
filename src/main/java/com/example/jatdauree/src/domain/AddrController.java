package com.example.jatdauree.src.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/")
public class AddrController {

    @GetMapping("**") // 모든 요청에 대해 GET 매핑
    public String handleAllGetRequests() {
        // GET 요청 처리 로직
        return "This is a GET request";
    }
    @PostMapping("**") // 모든 요청에 대해 GET 매핑
    public String handleAllPostRequests() {
        // GET 요청 처리 로직
        return "This is a Post request";
    }
    @PatchMapping("**") // 모든 요청에 대해 GET 매핑
    public String handleAllPatchRequests() {
        // GET 요청 처리 로직
        return "This is a Patch request";
    }

}
