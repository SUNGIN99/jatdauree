package com.umc.jatdauree.src.domain.app.menu.service;
import com.umc.jatdauree.src.domain.app.menu.dao.AppMenuDao;
import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.src.domain.app.menu.dto.GetMenuDetailInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Value;
import static com.umc.jatdauree.config.BaseResponseStatus.POST_STORES_NOT_REGISTERD;

@Service
public class AppMenuService {
    private final AppMenuDao appMenuDao;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client; //공부하기,자바 static

    @Autowired
    public AppMenuService(AppMenuDao appMenuDao,AmazonS3 s3Client) {
        this.appMenuDao = appMenuDao;
        this.s3Client = s3Client;
    }

    public GetMenuDetailInfoRes getMenuDetailInfo(int todaymenuIdx) throws BaseException{//getMenuDetailInfo
        //메뉴 클릭했을때 정보 가지고 오기
        //쿼라스트링으로 하나의 떨이메뉴와 해당 스토어 idx를 가지고 와야함 가지고 와야함
        try {
            GetMenuDetailInfoRes getMenuDetailInfoRes= appMenuDao.getMenuDetailInfo(todaymenuIdx);
            if (getMenuDetailInfoRes.getMenuUrl() != null && !getMenuDetailInfoRes.equals(""))
                getMenuDetailInfoRes.setMenuUrl("" + s3Client.getUrl(bucketName, getMenuDetailInfoRes.getMenuUrl()));
           return getMenuDetailInfoRes;
        }catch (Exception e){
            throw new BaseException(POST_STORES_NOT_REGISTERD); // 2030 : 사용자의 가게가 등록되어있지 않습니다.
        }
    }




}
