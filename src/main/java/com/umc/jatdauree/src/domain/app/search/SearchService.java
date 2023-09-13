package com.umc.jatdauree.src.domain.app.search;

import com.amazonaws.services.s3.AmazonS3;
import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.src.domain.app.basket.dto.GetStoreList;
import com.umc.jatdauree.src.domain.app.search.dto.GetSearchRes;
import com.umc.jatdauree.src.domain.app.search.dto.PostSearchReq;
import com.umc.jatdauree.src.domain.app.store.dto.StorePreviewRes;
import com.umc.jatdauree.src.domain.kakao.LocationValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.umc.jatdauree.config.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class SearchService {

    private final SearchDao searchDao;

    private final LocationValue locationValue;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public SearchService(SearchDao searchDao, AmazonS3 s3Client) {
        this.searchDao = searchDao;
        this.locationValue = new LocationValue();
        this.s3Client = s3Client;
    }

    @Transactional(rollbackFor = BaseException.class)
    public List<StorePreviewRes> postSearch(int userIdx, PostSearchReq searchReq) throws BaseException {
        // 가게 리스트 가져오기
        List<GetStoreList> storeList;
        try {
            storeList = searchDao.postSearch(userIdx, searchReq);
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);
        }

        List<StorePreviewRes> storePreviewRes = new ArrayList<>();
        for(GetStoreList store : storeList){
            // 가게 사진 URL
            if(store.getStoreLogoUrl() != null && store.getStoreLogoUrl().length() != 0){
                store.setStoreLogoUrl(""+s3Client.getUrl(bucketName, store.getStoreLogoUrl()));
            }
            if(store.getStoreSignUrl() != null && store.getStoreSignUrl().length() != 0){
                store.setStoreSignUrl(""+s3Client.getUrl(bucketName, store.getStoreSignUrl()));
            }

            // ** 내 위치에서 해당 가게까지의 거리 **
            int distance = (int) locationValue.getDistance(
                    searchReq.getLatitude(), searchReq.getLongitude(), store.getY(), store.getX());

            int duration = locationValue.getDuration(distance); // 분

            storePreviewRes.add(
                    new StorePreviewRes(
                            store.getStoreIdx(),
                            store.getStoreName(),
                            store.getStoreLogoUrl(),
                            store.getStoreSignUrl(),
                            store.getStar(),
                            distance, duration,
                            store.getCustomerIdx() != 0 ? 1:0
                    )
            );
        }

        // 검색기록 만들기
        try{
            searchDao.serachRecord(userIdx, searchReq);
        }catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);
        }

        return storePreviewRes;


    }

    public GetSearchRes getSearch(int userIdx) throws BaseException{
        // 최근 검색 기록
        List<String> recentSearch;
        try{
            recentSearch = searchDao.recentSearch(userIdx);
        }catch (Exception e) {
            System.out.println(e);
            throw new BaseException(RESPONSE_ERROR);
        }

        // 기준 시간
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String date = sdf.format(new Date());

        List<String> popularSearch;
        try{
            popularSearch = searchDao.popularSearch(date);
        }catch (Exception e) {
            System.out.println(e);
            throw new BaseException(RESPONSE_ERROR);
        }

        return new GetSearchRes(recentSearch, date, popularSearch);
    }
}
