package com.example.jatdauree.src.domain.store;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.store.dto.*;
import com.example.jatdauree.src.domain.store.service.StoreService;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
     * 23.07.014 작성자 : 김성인
     * 가게등록
     * GET /jat/stores/duplicate
     * @param @RequestParam storeName = ?
     * @return BaseResponse<postStoreRes>
     */
    @ResponseBody
    @GetMapping("/duplicate")
    public BaseResponse<StoreNameDupRes> storeNameDuplicate(@RequestParam String storeName){
        try{
            int sellerIdx = jwtService.getUserIdx();
            StoreNameDupRes storeNameDupRes = storeService.storeNameDuplicate(storeName);

            return new BaseResponse<>(storeNameDupRes);

        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.06 작성자 : 이윤채, 김성인
     * 가게등록
     * POST /jat/stores
     * @param @RequestBody PostStoreReq
     * @return BaseResponse<postStoreRes>
     */
    //가게정보등록
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostStoreRes> storeRegister(PostStoreReq postStoreReq) {
        try {
            int sellerIdx = jwtService.getUserIdx();
            PostStoreRes postStoreRes = storeService.storeRegister(sellerIdx, postStoreReq);

            return new BaseResponse<>(postStoreRes);

        } catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 23.07.07 작성자 :  김성인
     * 가게 간단 정보 조회
     * GET /jat/stores
     * @return BaseResponse<GetStoreInfoRes>
     */
    //가게정보등록
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetStoreInfoRes> getStoreInfo() {
        try {
            int sellerIdx = jwtService.getUserIdx();
            GetStoreInfoRes getStoreInfoRes = storeService.getStoreInfo(sellerIdx);

            return new BaseResponse<>(getStoreInfoRes);

        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }


    /**
     * 23.07.14 작성자 :김성인
     * 가게 등록 수정
     * Patch /jat/stores
     * @param @RequestBody PostStoreUpdateReq
     * @return BaseResponse<postStoreUpdateRes>
     */
    //가게정보 수정
    @ResponseBody
    @PatchMapping ("")
    public BaseResponse<PatchStoreInfoRes> storeUpdate(PatchStoreInfoReq patchStoreInfoReq){
        try {
            int sellerIdx = jwtService.getUserIdx();

            PatchStoreInfoRes patchStoreInfoRes = storeService.storeUpdate(sellerIdx, patchStoreInfoReq);
            return new BaseResponse<>(patchStoreInfoRes);
        }catch (BaseException baseException){
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /**
     * 23.07.12 작성자 : 이윤채
     * 가게 영업 종료
     * Patch /jat/stores/end
     * @param
     * @return BaseResponse<patchStoreEndRes>
     */
    @ResponseBody
    @PatchMapping ("/end")
    public BaseResponse<PatchStoreEndRes> storeEnd() {
        try {
            int sellerIdx = jwtService.getUserIdx();
            PatchStoreEndRes patchStoreEndRes = storeService.storeEnd(sellerIdx);    // 다오짜기
            return new BaseResponse<>(patchStoreEndRes);
        } catch (BaseException baseException){
            //System.out.println(" RESPONSE_ERROR5 : " + baseException.getMessage());
            return new BaseResponse<>(baseException.getStatus());
        }
    }

}


