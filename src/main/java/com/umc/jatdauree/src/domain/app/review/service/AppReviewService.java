package com.umc.jatdauree.src.domain.app.review.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.src.domain.app.review.dao.AppReviewDao;
import com.umc.jatdauree.src.domain.app.review.dto.*;
import com.umc.jatdauree.src.domain.web.order.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.umc.jatdauree.config.BaseResponseStatus.*;
import static com.umc.jatdauree.utils.UtilFileImgUrl.absolutePath;
import static com.umc.jatdauree.utils.UtilFileImgUrl.checkFileIsNullThenName;


@Service
public class AppReviewService {
    private final AppReviewDao appReviewDao;
    private final OrderDao orderDao;
    @Value("${cloud.aws.s3.bucket}") //버킷이름이 고정된 경우는 상관없지만 바뀌는 경우도 존재하니 properties 파일에서 버킷이름 읽어오기
    private String bucketName;
    private final AmazonS3 s3Client;

    @Autowired
    public AppReviewService(AppReviewDao appReviewDao, OrderDao orderDao, AmazonS3 s3Client){
        this.appReviewDao = appReviewDao;
        this.orderDao = orderDao;
        this.s3Client = s3Client;
    }


    //리뷰 작성하기
    @Transactional(rollbackFor = BaseException.class)
    public PostReviewRes postReview(int customerIdx, PostReviewReq postReviewReq) throws BaseException,IOException {
        // 1. 별점이 0점 이하이거나, 5점 이상인 경우는 예외처리
        if(postReviewReq.getStars() < 0 || postReviewReq.getStars() > 5){
            throw new BaseException(REQUEST_ERROR); // 별의 개수가 올바르지 않습니다.
        }

        // 2. 글을 작성하지 않은 경우 예외처리
        if(postReviewReq.getContents() == null || postReviewReq.getContents().equals("") ){
            throw new BaseException(REQUEST_ERROR); // 리뷰를 작성하지 않았습니다.
        }

        // 3. 주문의 상태가 완료여야만 리뷰를 작성 가능하다.
        String status;
        try {
            status = orderDao.checkOrderStatus(postReviewReq.getOrderIdx());
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 주문 정보가 올바르지 않다.
        }
        if(!"A".equals(status)){
            throw new BaseException(DATABASE_ERROR); // 리뷰를 작성할 수 없다.
        }

        // 4. getpostReivewReq.picture이 null이 아닌경우 S3 사용해서 Post하기
        String fileName;
        File file = null;
        // S3에 업로드될 고유한 이름 만들어 준다.
        try {
            fileName = checkFileIsNullThenName(postReviewReq.getReviewFile()); //S3에 업로드 할 이름을 지정해 준다.
            if (fileName != null) {
                file = new File(absolutePath + fileName); // 업로드될 경로를 지정해 준다.
            }
        }catch (Exception e){
            System.out.println("1");
            throw new BaseException(REQUEST_ERROR); // 리뷰 파일의 형태가 잘못됨
        }

        //서버에 업로드 하는 과정, S3에 업로드 하기 전에 서버에 임시로 저장해준다.
        try {
             if (file != null) {  //file의 이름이 만들어졌다면(client가 파일을 전송해서 이름이 만들어짐)
                 postReviewReq.getReviewFile().transferTo(file); //postReviewReq.getReviewFile 메서드를 호출해서 file에 transferTo로 서버에 업로드
             }
        }catch (Exception e){
            System.out.println("2");
            throw new BaseException(REQUEST_ERROR); // 리뷰 사진 파일이 잘못됐다.
        }

        // S3에 업로드 해주기
        try {
            if (fileName!= null) {
                s3Client.putObject(new PutObjectRequest(bucketName, fileName, file)); // s3Client.putObject로 버킷에 파일 업로드 fileName은 bucket에 저장 될 이름
                file.delete(); //url이 생성되면 file을 삭제한다.
            }
        } catch (Exception e) {
            throw new BaseException(S3_ACCESS_API_ERROR); // 5030: 이미지 URL 생성에 실패하였습니다.
        }

        //5. 리뷰 작성하기
        try{
            int reviewIdx = appReviewDao.reviewPost(customerIdx, postReviewReq, fileName);
            return new PostReviewRes(reviewIdx);
        }catch (Exception e){
            throw new BaseException(POST_REVIEW_COMMENT_DATA_UNVALID); // 리뷰를 등록하지 못했다.
        }
    }


    //모든 리뷰 조회하기 => + 몇달 전, 이거 나타내기
    public GetReviewRes myReviews(int customerIdx) throws BaseException{
        //1.  customerIdx의 reviewIdx개수를 세서 totalReviews로 나타내 준다.
        int totalReviews;
        try {
            totalReviews= appReviewDao.countReviews(customerIdx);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 리뷰의 개수를 세지 못함
        }
        //2. 리뷰들을 조회해 온다.
        List<MyReviews> myReviews;
        try {
            myReviews = appReviewDao.getMyReviews(customerIdx);
        } catch (Exception e){
            throw new BaseException(RESPONSE_ERROR); // 리뷰를 조회에 실패함
        }

        //3. 리뷰를 가져오는 중 사진이 !null인 경우 사진 가져옴
        try {
            for(MyReviews review : myReviews){
                if(review.getReviewUrl() != null && review.getReviewUrl().length() != 0)
                    review.setReviewUrl(""+s3Client.getUrl(bucketName, review.getReviewUrl())); //버킷 이름과 String Key(FileName)으로
            }
        }catch (Exception e){
            throw new BaseException(RESPONSE_ERROR); // 사진 조회에 실패하였습니다.
        }

        // 4. 리뷰의 날짜는 방금 전, 몇일 전, 몇개월 전, 몇년 전으로 표시
        try{
            LocalDateTime now = LocalDateTime.now(); //현재 시간
            for(MyReviews reviews : myReviews){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //DAO에서 받아오는 getDate와 형식을 맞추어 준다.
                LocalDateTime reviewDate = LocalDateTime.parse(reviews.getDate(),formatter);

                Duration duration = Duration.between(reviewDate, now); // 현재 시간과 DB에서 가져온 getDate 사이의 시간

                long years = duration.toDays() / 365;
                long months = duration.toDays() / 30;
                long days = duration.toDays();
                long hours = duration.toHours();
                long minutes = duration.toMinutes();

                if (years > 0) {
                    reviews.setDate(years + "년 전");
                } else if (months > 0) {
                    reviews.setDate(months + "개월 전");;
                } else if (days > 0) {
                    reviews.setDate(days + "일 전");
                } else if (hours > 0) {
                    reviews.setDate(hours + "시간 전");
                } else if (minutes > 0) {
                    reviews.setDate(minutes + "분 전");
                } else {
                    reviews.setDate("방금 전");
                }
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); //날짜를 잘못 출력했습니다.
        }

        //5. 리뷰의 메뉴를 리스트로 받아온다.
        try {
            for(MyReviews review : myReviews){
                List<String> reviewMenus = appReviewDao.myReviewsMenu(review.getReviewIdx());
                review.setReviewMenus(reviewMenus);
            }
            return new GetReviewRes(totalReviews, myReviews);
        }catch (Exception e){
            throw new BaseException(RESPONSE_ERROR); //메뉴 리스트 조회에 실패 함.
        }
    }


    //리뷰 삭제하기
    @Transactional(rollbackFor = BaseException.class)
    public PatchReviewRes deleteReview(int customerIdx, PatchReviewReq patchReviewReq) throws BaseException{
        //1. 리뷰 상태 확인하고 상태가 A인 경우만 삭제가능, D인 경우와 R인 경우도 존재
        String status;
        try {
            status = appReviewDao.checkReviewStatus(patchReviewReq.getReviewIdx());
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); //리뷰의 상태를 알 수 없다.
        }

        if("D".equals(status)){
            throw new BaseException(DATABASE_ERROR); //이미 신고/삭제 된 리뷰이다.
        }
        //2. 리뷰 삭제하기
        int reivewDel;
        try {
            reivewDel = appReviewDao.reviewDelete(patchReviewReq.getReviewIdx());
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);// 리뷰가 삭제되지 않음
        }

        //3. 리뷰가 삭제되었는지 확인하기
        if (reivewDel == 1)
            return new PatchReviewRes(patchReviewReq.getReviewIdx(), reivewDel);
        else
            throw new BaseException(RESPONSE_ERROR);
    }

    //리뷰 신고하기
    @Transactional(rollbackFor = BaseException.class)
    public ReportReviewRes reportReview(ReportReviewReq reportReviewReq) throws BaseException{
        //1.리뷰의 상태가 A인 경우만 작성가능하다. (D이거나 R인 경우 신고 못함)
        String status;
        try{
            status = appReviewDao.checkReviewStatus(reportReviewReq.getReviewIdx());
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); // 리뷰 상태를 알 수 없음
        }
        if(!"A".equals(status)){
            throw new BaseException(REVIEW_ALREADY_DONE); //이미 신고/삭제 된 리뷰이다.
        }

        //2.리뷰를 신고 상태"R"로 바꾸기 (원래 storeIdx도 있었는데 필요없는 것 같아서 뺐다.)
        int reviewReport;
        try {
            reviewReport = appReviewDao.reportReview(reportReviewReq.getReviewIdx());
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        if (reviewReport == 1){
            return new ReportReviewRes(reportReviewReq.getReviewIdx(), reviewReport);
        }
        else {
            throw new BaseException(DATABASE_ERROR);
        }


    }

    public ReviewReady reviewReady(int orderIdx) throws BaseException{
        try{
            return appReviewDao.reviewReady(orderIdx);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
