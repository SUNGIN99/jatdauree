package com.umc.jatdauree.src.domain.web.payment;

import com.umc.jatdauree.config.BaseException;
import com.umc.jatdauree.config.secret.IamPortSecret;
import com.umc.jatdauree.src.domain.web.payment.dto.DeleteBillingReq;
import com.umc.jatdauree.src.domain.web.payment.dto.DeleteBillingRes;
import com.umc.jatdauree.src.domain.web.payment.dto.IssueBillingReq;
import com.umc.jatdauree.src.domain.web.payment.dto.IssueBillingRes;
import com.umc.jatdauree.src.domain.web.payment.iamportDto.BillingKeyFoundation;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.BillingCustomerData;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static com.umc.jatdauree.config.BaseResponseStatus.BILLING_API_ERROR;

@Service
public class PaymentService {

    private final IamportClient iamportClient;
    @Autowired
    private final PaymentDao paymentDao;

    public PaymentService(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
        this.iamportClient = new IamportClient(IamPortSecret.iamPortApiKey, IamPortSecret.iamPortApiSecret);
    }

    private String createNewCustomerUid(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    @Transactional
    public IssueBillingRes issueBilling(IssueBillingReq issueBillingReq) throws BaseException {
        BillingCustomerData billingCustomerData = new BillingCustomerData(
                null,
                issueBillingReq.getCard_number(),
                issueBillingReq.getExpiry(),
                issueBillingReq.getBirth()
        );
        // 1) iamPort API 인가 토큰 발행 -> API 사용하려면 반드시 필요한 값
        AccessToken auth;
        try{
            auth = iamportClient.getAuth().getResponse();
        }catch (Exception e){
            throw new BaseException(BILLING_API_ERROR); // iamport 서버 인가토큰 발행 실패
        }

        // 2) 랜덤 Customer_uid (빌링키에 매핑되는) 생성
        // 원래 있던 값을 재 사용해버리면 카드 매핑정보가 덮어씌워짐.. ***
        String newCustomerUid;
        try{
            newCustomerUid = createNewCustomerUid();

            // DB에 해당 빌링키 존재하는지 중복 확인
            // EXIST 1이면 존재, 0이면 존재 안함
            while(paymentDao.checkExsitBillingKey(newCustomerUid) == 1){
                newCustomerUid = createNewCustomerUid();
            }
            System.out.println("랜덤생성: " +newCustomerUid);
        }catch (Exception e){
            throw new BaseException(BILLING_API_ERROR); // 랜덤 customer_uid(빌링키 매핑) 생성 오류
        }

        // 3) 빌링키 발행
        BillingKeyFoundation billingKeyFoundation;
        try{
            billingCustomerData.setPg("kcp.T0000");
            IamportResponse<BillingCustomer> billingCustomerInfo =
                    iamportClient.postBillingCustomer(
                            newCustomerUid,
                            billingCustomerData);

            System.out.println(billingCustomerInfo.getResponse().toString());
            billingKeyFoundation = new BillingKeyFoundation(
                            billingCustomerInfo.getResponse().getCustomerUid(), // 빌링키 매핑 id
                            billingCustomerInfo.getResponse().getCardName(),    // 카드 이름
                            issueBillingReq.getCardNickName(),  // 카드 별칭
                            issueBillingReq.getPw6());   // 비밀번호 6자리
        }catch (Exception e){
            throw new BaseException(BILLING_API_ERROR); // 빌링키 발행 실패
        }

        // 4) 빌링키 서버 저장
        try{
            int billIdx = paymentDao.issueBilling(billingKeyFoundation);
            return new IssueBillingRes(billIdx);
        }catch (Exception e){
            throw new BaseException(BILLING_API_ERROR); // 빌링키 앱 서버 DB저장 실패
        }
    }

    public DeleteBillingRes deleteBilling(DeleteBillingReq deleteBillingReq) throws BaseException {
        try{
            IamportResponse<BillingCustomer> billingCustomerInfo =
                    iamportClient.deleteBillingCustomer(
                            deleteBillingReq.getCustomer_uid(),
                            "테스트", "본인");

            System.out.println(billingCustomerInfo.getResponse().toString());

            return new DeleteBillingRes(
                    billingCustomerInfo.getResponse().getCustomerUid(),
                    billingCustomerInfo.getResponse().getPgProvider(),
                    billingCustomerInfo.getResponse().getPgId(),
                    billingCustomerInfo.getResponse().getCardName(),
                    billingCustomerInfo.getResponse().getCardCode(),
                    billingCustomerInfo.getResponse().getCardNumber(),
                    billingCustomerInfo.getResponse().getCardType(),
                    billingCustomerInfo.getResponse().getCustomerName(),
                    billingCustomerInfo.getResponse().getCustomerTel(),
                    billingCustomerInfo.getResponse().getCustomerEmail(),
                    billingCustomerInfo.getResponse().getCustomerAddr(),
                    billingCustomerInfo.getResponse().getCustomerPostcode(),
                    billingCustomerInfo.getResponse().getInserted(),
                    billingCustomerInfo.getResponse().getUpdated());
        }catch(Exception e){
            throw new BaseException(BILLING_API_ERROR);
        }
    }
}
