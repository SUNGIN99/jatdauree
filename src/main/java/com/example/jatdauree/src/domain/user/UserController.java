package com.example.jatdauree.src.domain.user;


import com.example.jatdauree.src.domain.user.dto.PostLoginRequest;
import com.example.jatdauree.src.domain.user.dto.UserAuthentication;
import com.example.jatdauree.src.domain.user.service.UserService;
import com.example.jatdauree.utils.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/jat/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public String login(@RequestBody PostLoginRequest postLoginRequest){
        log.info("request: user id = {}", postLoginRequest.getUid());
        UserAuthentication user = (UserAuthentication) userService.loadUserByUserIdx(1L);
        log.info("DB Access: user id = {}", user.getUid());

        return jwtTokenProvider.createJwt(user.getSellerIdx().intValue());
    }
}
