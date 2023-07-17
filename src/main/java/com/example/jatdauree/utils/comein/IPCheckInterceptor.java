package com.example.jatdauree.utils.comein;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Constants;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class IPCheckInterceptor implements HandlerInterceptor, Constants {

    private static final String LOCAL_HOST = "127.0.0.1";
    @Autowired
    private WSOpenAPIService WSService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String clientIp = request.getHeader("X-Forwarded-For");
        if (ObjectUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (ObjectUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ObjectUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ObjectUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ObjectUtils.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        //로컬 테스트 시 주석 해제해주세요. 미국 IP입니다.
        //clientIp = "54.211.120.28";

        if(!LOCAL_HOST.equals(clientIp)) {
            Map<String,String> clientInfo = WSService.getClientInfoByIPAddress(clientIp);

            if(clientInfo == null) {
                logger.error("IP Info Not Found");
                return false;
            }

            String country = clientInfo.get("countryCode");

            if(!"KR".equals(country)) {
                logger.error("Abroad IP detected - IP : {}, Country : {}", clientIp, country);
                return false;
            }
        }

        return true;
    }
}
