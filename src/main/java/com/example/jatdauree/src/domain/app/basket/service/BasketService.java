package com.example.jatdauree.src.domain.app.basket.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.basket.dao.BasketDao;
import com.example.jatdauree.src.domain.app.basket.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.*;

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
            System.out.println("1:" + e);
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

    @Transactional(rollbackFor = BaseException.class)
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
        // 1. 장바구니 조회
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
                    totalMenuPrice += basketItem.getTodayPrice() * basketItem.getCount(); // 총 주문 가격
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
                basketRes.setStoreName(basketItemsDao.get(0).getStoreName());
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

    public BasketOrderRes getBasketOrder(int userIdx) throws BaseException{
        try{
            BasketOrderRes orderRes = basketDao.getBasketOrder(userIdx);
            return orderRes;
        }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
            throw new BaseException(NO_BASKET_ITEMS);// 장바구니에 담긴 항목없이 주문 불가능합니다.
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = BaseException.class)
    public OrderDoneRes postBasketOrder(int userIdx, OrderDoneReq orderReq) throws BaseException {
        // 1) 주문하기 전 장바구니에 담긴 품목 리스트 가져오기
        int storeIdx = orderReq.getStoreIdx();
        List<BasketOrderItem> basketItems;
        try {
            basketItems = basketDao.getBasketOrderItems(userIdx);
        } catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        // 2) 해당 장바구니 목록들이 가게에 충분히 남아있는지 확인인
        try{
            for (BasketOrderItem bItem : basketItems){
                BasketTodayMenu bTodayMenu = basketDao.checkItemRemain(storeIdx, bItem.getTodayMenuIdx(), bItem.getCnt());
                // 조회한 개수가 가게에 남아있는 개수보다 크다면..?
                if(bTodayMenu.getRemain() < bItem.getCnt() || bTodayMenu.getTodayMenuIdx() != bItem.getTodayMenuIdx()){
                    throw new IncorrectResultSizeDataAccessException(0);
                }
            }
        }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
            throw new BaseException(STORE_TODAY_MENU_LACK); // 가게의 떨이메뉴 개수가 부족하여 주문이 불가능합니다.
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        String pickupDate = sdf.format(new Date());
        orderReq.setPickupTime(pickupDate + orderReq.getPickupTime());

        try{
            // 주문 정보
            int orderIdx = basketDao.postBasketOrder(userIdx, orderReq);

            // 주문에 들어가는 메뉴 정보
            int orderMenuCnt = basketDao.postBasketOrderItems(orderIdx, basketItems);
            /*System.out.println("orderMenuCnt: " +orderMenuCnt);
            if (orderMenuCnt != basketItems.size())
                throw new Exception();*/

            // 주문한만큼 오늘의 메뉴 개수 감소
            int tmDescCnt = basketDao.todayMenuDecrease(basketItems);
            /*System.out.println("tmDescCnt: " +tmDescCnt);
            if (tmDescCnt != basketItems.size())
                throw new Exception();*/

            // 주문햇으면 장바구니에 있는 아이템 전부 비활성화
            int basketCount = basketDao.basketItemDone(basketItems);
            //System.out.println("basketCount: " +basketCount);

            return new OrderDoneRes(orderIdx, basketItems.size());
        }catch(Exception e){
            throw new BaseException(ORDER_FAILED);
        }
    }
}
