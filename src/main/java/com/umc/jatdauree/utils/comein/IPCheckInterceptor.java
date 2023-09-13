package com.umc.jatdauree.utils.comein;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.umc.jatdauree.utils.comein.HttpReqRespUtils.allHeaderOutput;

@Component
public class IPCheckInterceptor implements HandlerInterceptor, Constants {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        allHeaderOutput();
        return true;
    }
}
