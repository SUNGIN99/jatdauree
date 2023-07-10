package com.example.jatdauree.src.domain.sales.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.sales.dto.TodayTotalSalesRes;
import com.example.jatdauree.src.domain.sales.dao.SalesDao;
import com.example.jatdauree.src.domain.sales.dto.YtoTdaySalesRes;
import com.example.jatdauree.src.domain.store.dao.StoreDao;
import com.example.jatdauree.src.domain.store.dto.StoreStartClose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.jatdauree.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.jatdauree.config.BaseResponseStatus.POST_STORES_NOT_REGISTERD;

@Service
public class SalesService {

    private SalesDao salesDao;
    private StoreDao storeDao;

    @Autowired
    public SalesService(SalesDao salesDao, StoreDao storeDao) {
        this.salesDao = salesDao;
        this.storeDao = storeDao;
    }


    // 가게 오늘 총 매출량
    public TodayTotalSalesRes getTodayTotalSales(int sellerIdx) throws BaseException {
        // 0) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 1) 가게의 오늘 매출 조회
        try{
            TodayTotalSalesRes totalSales = salesDao.getTodayTotalSales(storeIdx);
            return totalSales;
        }catch (Exception e) {
            // 오늘 매출량이 없을 수도 있음.
            return new TodayTotalSalesRes(
                    sellerIdx,
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                    0);
            //throw new BaseException(DATABASE_ERROR); //  가게의 오늘 총 매출 조회에 실패하였습니다.
        }


    }

    public YtoTdaySalesRes getFromYtoTdaySales(int sellerIdx) throws BaseException{
        // 0) 사용자 가게 조회
        int storeIdx;
        try{
            storeIdx = storeDao.storeIdxBySellerIdx(sellerIdx);
        } catch (Exception e) {
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }

        // 1) 판매자 가게의 시작/종료 시간 확인
        StoreStartClose storeStartClose;
        try{
           storeStartClose = storeDao.getStoreOpenNClose(storeIdx);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR); //  : 판매자의 가게 매출 확인용 시간 범위 조회에 실패하였습니다.
        }


        String yesterday = new SimpleDateFormat("yyyy-MM-dd")
                        .format(new Date().getTime() + 1000 * 60 * 60 * 24 * -1);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        // 2) 어제 영업 시간 중 판매량

        // 3) 오늘 영업 시간 중 판매량

    }
}
