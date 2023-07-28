package com.example.jatdauree.src.domain.app.basket.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.basket.dao.BasketDao;
import com.example.jatdauree.src.domain.app.basket.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class BasketService {

    private final BasketDao basketDao;

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    public BasketService(BasketDao basketDao, AmazonS3 s3Client) {
        this.basketDao = basketDao;
        this.s3Client = s3Client;
    }



    public BasketStoreCheckRes storeCheck(int customerIdx, BasketStoreCheckReq checkReq) throws BaseException {
        // 1) 장바구니 담겨있는지 먼저 조회
        // 같은 가게만 장바구니 담을 수 있도록 하기 위함.
        List<BasketExist> basketExist;
        try{
            basketExist = basketDao.checkBaketAlreadyExists(customerIdx);
        }
        catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 장바구니 조회 실패
        }

        // 2) 장바구니 추가 방식 설정
        // 2-1) 현재 담아놓은 장바구니가 없을 때
        if (basketExist.size() == 0){
            // sameStoreCheck = 0 이면 그냥 장바구니 담기 가능
            return new BasketStoreCheckRes(0, 0);
        }
        // 2-2) 현재 담아놓은 장바구니가 있을 때
        else{
            // 2-2-1) 같은 가게 메뉴를 장바구니에 담을때
            if(basketExist.get(0).getStoreIdx() == checkReq.getStoreIdx()){
                // sameStoreCheck = 0 이면 그냥 장바구니 담기 가능
                return new BasketStoreCheckRes(basketExist.get(0).getStoreIdx(), 0);
            }
            // 2-2-2) 다른 가게 메뉴를 장바구니에 담을때
            else {
                // sameStoreCheck = 1 이면 같은 가게만 담기 가능하다고 알림
                return new BasketStoreCheckRes(basketExist.get(0).getStoreIdx(), 1);

            }
        }
    }

    public PostBasketRes postBasket(int customerIdx, PostBasketReq basketReq) throws BaseException {
        int basketIdx = 0;
        try{
            if (basketReq.getSameStoreCheck() == 1){
                basketDao.renewBasket(customerIdx); // 원래 담아놨던 장바구니 삭제
            }
            basketIdx = basketDao.postBasket(customerIdx, basketReq);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 장바구니에 메뉴 담기를 실패하였습니다.
        }

        return new PostBasketRes(basketIdx);
    }


    public GetBasketRes getBasket(int customerIdx) throws BaseException{
        List<BasketItemFromDao> basketItemsDao;
        try{
            basketItemsDao = basketDao.getBasket(customerIdx);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 장바구니에 조회실패.
        }

        // API 응답 형태로 전환
        // 총 메뉴 및 가격 만들기
        List<BasketItem> basketItems = new ArrayList<>();
        int totalMenuCount = 0, totalMenuPrice = 0;
        try{
            if(basketItemsDao.size() != 0){
                for (BasketItemFromDao basketItem :basketItemsDao){
                    totalMenuCount += 1; // 메뉴 종류 개수
                    totalMenuCount += basketItem.getTodayPrice() * basketItem.getCount(); // 총 주문 가격
                    basketItems.add(
                            new BasketItem(basketItem.getStoreIdx(),
                                    basketItem.getTodaymenuIdx(),
                                    basketItem.getMenuUrl(),
                                    basketItem.getMenuName(),
                                    basketItem.getCount(),
                                    basketItem.getPrice(),
                                    basketItem.getDiscount(),
                                    basketItem.getTodayPrice())
                    );
                }
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 장바구니 반환 실패
        }

        // menu url 가져오기
        try{
            if(basketItems.size() != 0){
                for(BasketItem basketItem : basketItems){
                    if (basketItem.getMenuUrl() != null && !basketItem.getMenuUrl().equals("")){
                        basketItem.setMenuUrl(""+s3Client.getUrl(bucketName, basketItem.getMenuUrl()));
                    }
                }
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 메뉴 사진 조회 실패
        }

        try{
            GetBasketRes basketRes = new GetBasketRes(0, null, 0,0, null);
            if(basketItemsDao.size() != 0){
                basketRes.setStoreIdx(basketItemsDao.get(0).getStoreIdx());
                basketRes.setStoreName(basketRes.getStoreName());
                basketRes.setTotalMenuCount(totalMenuCount);
                basketRes.setTotalMenuPrice(totalMenuPrice);
                basketRes.setBasketItems(basketItems);
            }
            return basketRes;
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 장바구니 결과 에러
        }
    }

    public GetBasketCountRes getBasketCount(int customerIdx) throws BaseException {
        try{
            int basketcount = basketDao.getBasketCount(customerIdx);
            return new GetBasketCountRes(basketcount);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 장바구니 결과 에러
        }
    }
}
