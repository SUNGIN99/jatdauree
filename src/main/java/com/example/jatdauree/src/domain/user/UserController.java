package com.example.jatdauree.src.domain.user;


import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.user.dto.PostLoginRequest;
import com.example.jatdauree.src.domain.user.dto.UserAuthentication;
import com.example.jatdauree.src.domain.user.service.UserService;
import com.example.jatdauree.utils.JwtService;
import com.example.jatdauree.utils.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/jat/users")
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, JwtService jwtService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(@RequestBody PostLoginRequest postLoginRequest){
        log.info("request: user id = {}", postLoginRequest.getUid());
        UserAuthentication user = (UserAuthentication) userService.loadUserByUserIdx(1L);
        log.info("DB Access: user id = {}", user.getUid());

        return jwtTokenProvider.createJwt(user.getSellerIdx().intValue());
    }

    @GetMapping("/tests")
    public int authenticationTest() throws BaseException {
        int userId = jwtService.getUserIdx();
        log.info("userIdx = {} " , userId);
        return userId;

    }
}
