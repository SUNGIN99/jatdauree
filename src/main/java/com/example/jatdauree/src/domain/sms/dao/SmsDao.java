package com.example.jatdauree.src.domain.sms.dao;

import com.example.jatdauree.src.domain.seller.dto.PostSignUpAuthyReq;
import com.example.jatdauree.src.domain.seller.dto.ReceivedNumConfReq;
import com.example.jatdauree.src.domain.seller.dto.SmsCertificateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class SmsDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * SmsDao - 1
     * 23.07.02 작성자 : 김성인
     * ID 찾기할때 인증번호 전송 정보 저장
     * 넣을때 ID 찾기는 이름, 전화번호만  checkType = 'I'
     * PW 찾기는 아이디, 전화번호만 입력됨  checkType = 'P'
     * 전화번호는 무조건 입력,
     */
    public int smsSend(SmsCertificateReq smsCertificateReq, String certificationNum, String checkType){
        String query = "INSERT INTO Sms(phone, name, uid, certification_num, status)" +
                "VALUES(?, ?, ?, ?, ?);";

        Object[] params = new Object[]{
                smsCertificateReq.getPhoneNum(),
                smsCertificateReq.getName() == null ? null : smsCertificateReq.getName(),
                smsCertificateReq.getUid() == null ? null : smsCertificateReq.getUid(),
                certificationNum,
                checkType
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /**
     * SmsDao - 2
     * 23.07.02 작성자 : 김성인
     * 아이디 찾기 시, 유효한 인증번호 확인
     * 서버에서 인증번호 보낸기록, 3분이 아직 덜 지났는지
     */
    public int smsCheckId(ReceivedNumConfReq receivedNumConfReq) {
        String query = "SELECT EXISTS(\n" +
                "    SELECT\n" +
                "        * \n" +
                "    FROM Sms WHERE phone = ?\n" +
                "               AND name = ?\n" +
                "               AND certification_num = ?\n" +
                "               AND status = 'I'\n" +
                "               AND created >= DATE_ADD(NOW(), INTERVAL -3 MINUTE) \n" +
                "            ORDER BY created DESC LIMIT 1" +
                "    )";

        Object[] params = new Object[]{
                receivedNumConfReq.getPhoneNum(),
                receivedNumConfReq.getName(),
                receivedNumConfReq.getCertificationNum()
        };

        return this.jdbcTemplate.queryForObject(query, int.class, params);

    }

    /**
     * SmsDao - 3
     * 23.07.02 작성자 : 김성인
     * 비밀번호 찾기 시, 유효한 인증번호 확인
     * 서버에서 인증번호 보낸기록, 3분이 아직 덜 지났는지
     */
    public int smsCheckPw(ReceivedNumConfReq receivedNumConfReq){
        String query = "SELECT EXISTS(\n" +
                "    SELECT\n" +
                "        *\n" +
                "    FROM Sms WHERE phone = ? \n" +
                "               AND uid = ? \n" +
                "               AND certification_num = ? \n" +
                "               AND status = 'P'\n" +
                "               AND created >= DATE_ADD(NOW(), INTERVAL -3 MINUTE) \n" +
                "            ORDER BY created DESC LIMIT 1" +
                "    )";

        Object[] params = new Object[]{
                receivedNumConfReq.getPhoneNum(),
                receivedNumConfReq.getUid(),
                receivedNumConfReq.getCertificationNum()
        };

        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    /**
     * SmsDao - 4
     * 23.07.02 작성자 : 김성인
     * ID 찾기할때 인증번호 전송 정보 저장
     * 넣을때 ID 찾기는 이름, 전화번호만  checkType = 'I'
     * PW 찾기는 아이디, 전화번호만 입력됨  checkType = 'P'
     * 전화번호는 무조건 입력,
     */
    public int smsAuthy(PostSignUpAuthyReq signUPValid, String certificationNum, String checkType){
        String query = "INSERT INTO Sms(phone, name, uid, certification_num, status)" +
                "VALUES(?, ?, ?, ?, ?);";

        Object[] params = new Object[]{
                signUPValid.getPhoneNum(),
                signUPValid.getName(),
                signUPValid.getBirth(),
                certificationNum,
                checkType
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /**
     * SmsDao - 5
     * 23.07.02 작성자 : 김성인
     * ID 찾기할때 인증번호 전송 정보 저장
     * 넣을때 ID 찾기는 이름, 전화번호만  checkType = 'I'
     * PW 찾기는 아이디, 전화번호만 입력됨  checkType = 'P'
     * 전화번호는 무조건 입력,
     */
    public int smsAuthyPass(PostSignUpAuthyReq signUPValid){
        String query = "SELECT EXISTS(\n" +
                "    SELECT\n" +
                "        *\n" +
                "    FROM Sms\n" +
                "    WHERE phone = ? AND\n" +
                "          name = ? AND\n" +
                "          uid = ? AND\n" +
                "          certification_num = ? AND\n" +
                "          status = 'S' AND\n" +
                "          created >= DATE_ADD(NOW(), INTERVAL -3 MINUTE)\n" +
                "    ORDER BY created DESC LIMIT 1)";

        Object[] params = new Object[]{
                signUPValid.getPhoneNum(),
                signUPValid.getName(),
                signUPValid.getBirth(),
                signUPValid.getCertificationNum(),
        };

        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }
}
