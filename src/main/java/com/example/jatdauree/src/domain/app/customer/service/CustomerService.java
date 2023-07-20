package com.example.jatdauree.src.domain.app.customer.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.customer.dao.CustomerDao;
import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpReq;
import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpRes;
import com.example.jatdauree.src.domain.web.sms.dao.SmsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.jatdauree.config.BaseResponseStatus.*;
import static com.example.jatdauree.config.BaseResponseStatus.POST_USERS_EXISTS_ID;
import static com.example.jatdauree.utils.ValidationRegex.*;
import static com.example.jatdauree.utils.ValidationRegex.isRegexPhone;

@Service
public class CustomerService {

    private final SmsDao smsDao;
    private final CustomerDao customerDao;

    @Autowired
    public CustomerService(SmsDao smsDao, CustomerDao customerDao) {
        this.smsDao = smsDao;
        this.customerDao = customerDao;
    }

    @Transactional(rollbackFor = BaseException.class)
    public PostSignUpRes signUp(PostSignUpReq postSignUpReq) throws BaseException {

        // 아이디, 비밀번호, 닉네임 정규식 처리
        if (postSignUpReq.getUid().length() == 0 || postSignUpReq.getPassword().length() == 0 || postSignUpReq.getName().length() == 0) {
            throw new BaseException(REQUEST_ERROR); // 2000 : 입력값 전체 빈 값일때 ( 입력값을 확인해주세요.)
        }
        if (!isRegexUid(postSignUpReq.getUid())) {
            throw new BaseException(POST_USERS_INVALID_UID); // 2010 : 아이디 정규 표현식 예외 (아이디를 다시 확인해주세요.)
        }
        if (!isRegexPassword(postSignUpReq.getPassword())) {
            throw new BaseException(POST_USERS_INVALID_PASSWORD); // 2011 : 비밀번호 정규 표현식 예외 (비밀번호를 다시 확인해주세요.)
        }
        // 올바른 생년월일 양식인지?
        if (!isRegexBirth(postSignUpReq.getBirthday())) {
            throw new BaseException(POST_USERS_INVALID_BIRTHDAY); //2013 :(생년월일 양식을 yyyy.MM.dd로 설정해주세요.)
        }
        if (!isRegexPhone(postSignUpReq.getPhone())) {
            throw new BaseException(POST_USERS_INVALID_PHONENUM); // 2014 : (전화번호는 - 없이 숫자만 입력해주세요.)
        }
        // 중복 아이디 체크
        if (customerDao.checkUid(postSignUpReq.getUid()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID); // 2018 : 중복 아이디
        }

        //salt를 이용하여 암호화를 하고 싶은데 Customer table에 salt열이 없어 사용 못함.
        try {
            // 유저 고유식별번호
            int customerIdx = customerDao.signup(postSignUpReq);

            PostSignUpRes postSignUpres = customerDao.signUpComplete(customerIdx);
            postSignUpres.setCompleteDate(postSignUpres.getCompleteDate().substring(0, 10)); //2020년10월02

            return postSignUpres;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);

        }

    }
}
