package com.example.jatdauree.src.domain.store;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.store.dto.PostStoreReq;
import com.example.jatdauree.src.domain.store.dto.PostStoreRes;
import com.example.jatdauree.src.domain.store.dto.PostStoreUpdateReq;
import com.example.jatdauree.src.domain.store.dto.PostStoreUpdateRes;
import com.example.jatdauree.src.domain.store.service.StoreService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jat/stores")
public class StoreController {

    private final JwtService jwtService;
    private final StoreService storeService;

    @Autowired
    public StoreController(JwtService jwtService, StoreService storeService) {
        this.jwtService = jwtService;
        this.storeService = storeService;
    }


    /**
     * StoreController - 1
     * 23.07.06 작성자 : 이윤채, 김성인
     * storeRegister 가게등록  POST
     * POST/jat/stores
     * @param @RequestBody PostStoreReq
     * @return BaseResponse<postStoreRes>
     */
    //가게정보등록
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostStoreRes> storeRegister(@RequestBody PostStoreReq postStoreReq) {
        try {
            int sellerIdx = jwtService.getUserIdx();
             PostStoreRes postStoreRes = storeService.storeRegister(sellerIdx, postStoreReq);

            return new BaseResponse<>(postStoreRes);

        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * StoreController - 2
     * 23.07.06 작성자 : 이윤채
     * storeUpdate 가게 등록 수정 POST
     * POST/jat/menu/storeUpdate
     * @param @RequestBody PostStoreUpdateReq
     * @return BaseResponse<postStoreUpdateRes>
     */
    //가게정보 수정
    @ResponseBody
    @PostMapping("/storeUpdate")
    public BaseResponse<PostStoreUpdateRes> storeUpdate(@RequestBody PostStoreUpdateReq postStoreUpdateReq){
        try {
            PostStoreUpdateRes postStoreUpdateRes =storeService.storeUpdate(postStoreUpdateReq);
            return new BaseResponse<>(postStoreUpdateRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

}


