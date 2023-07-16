package com.example.jatdauree.utils.comein;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Objects;

@Slf4j
public class HttpReqRespUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public static String getClientIpAddressIfServletRequestExist() {

        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            return "0.0.0.0";
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (String header: IP_HEADER_CANDIDATES) {
            String ipFromHeader = request.getHeader(header);
            if (Objects.nonNull(ipFromHeader) && ipFromHeader.length() != 0 && !"unknown".equalsIgnoreCase(ipFromHeader)) {
                String ip = ipFromHeader.split(",")[0];
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static void allHeaderOutput(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        log.info("allHeaderOutput()->>");
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = (String) headerNames.nextElement();
                String value = request.getHeader(name);
                log.info("Header ::: " + name + " : " + value);
            }
        }
    }
}