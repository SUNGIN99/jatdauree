package com.example.jatdauree.src.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.example.jatdauree.utils.comein.HttpReqRespUtils.allHeaderOutput;
import static com.example.jatdauree.utils.comein.HttpReqRespUtils.getClientIpAddressIfServletRequestExist;

@Slf4j
@RestController
@RequestMapping("/")
public class AddrController {

    @GetMapping("**") // 모든 요청에 대해 GET 매핑
    public String handleAllGetRequests() {
        String remoteAddr = getClientIpAddressIfServletRequestExist();
        log.info("getIpAddIfserveletReqExist: " + remoteAddr);

        allHeaderOutput();
        return "This is a GET request";
    }
    @PostMapping("**") // 모든 요청에 대해 GET 매핑
    public String handleAllPostRequests() {
        String remoteAddr = getClientIpAddressIfServletRequestExist();
        log.info("getIpAddIfserveletReqExist: " + remoteAddr);

        allHeaderOutput();
        return "This is a Post request";
    }
    @PatchMapping("**") // 모든 요청에 대해 GET 매핑
    public String handleAllPatchRequests() {
        String remoteAddr = getClientIpAddressIfServletRequestExist();
        log.info("getIpAddIfserveletReqExist: " + remoteAddr);

        allHeaderOutput();
        return "This is a Patch request";
    }

}
