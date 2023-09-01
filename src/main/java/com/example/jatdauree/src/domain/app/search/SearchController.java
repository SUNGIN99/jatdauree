package com.example.jatdauree.src.domain.app.search;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.config.BaseResponse;
import com.example.jatdauree.src.domain.app.search.dto.GetSearchRes;
import com.example.jatdauree.src.domain.app.search.dto.PostSearchReq;
import com.example.jatdauree.src.domain.app.store.dto.StorePreviewRes;
import com.example.jatdauree.utils.jwt.JwtService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/jat/app/search")
public class SearchController {

    private final JwtService jwtService;
    private final SearchService searchService;

    public SearchController(JwtService jwtService, SearchService searchService) {
        this.jwtService = jwtService;
        this.searchService = searchService;
    }

    @ResponseBody
    @PostMapping("")
    BaseResponse<List<StorePreviewRes>> postSearch(@RequestBody PostSearchReq searchReq){
        try{
            int userIdx = jwtService.getUserIdx();
            List<StorePreviewRes> searchRes = searchService.postSearch(userIdx, searchReq);
            return new BaseResponse<>(searchRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    BaseResponse<GetSearchRes> getSearch(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetSearchRes searchRes = searchService.getSearch(userIdx);
            return new BaseResponse<>(searchRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}
