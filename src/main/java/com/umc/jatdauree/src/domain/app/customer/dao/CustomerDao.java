package com.umc.jatdauree.src.domain.app.customer.dao;


import com.umc.jatdauree.src.domain.app.customer.dto.*;
import com.umc.jatdauree.src.domain.web.seller.dto.PostLoginReq;
import com.umc.jatdauree.src.domain.web.seller.dto.PostSignUpAuthyReq;

import com.umc.jatdauree.src.domain.web.seller.dto.SmsCertificateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Repository
public class CustomerDao {

    private JdbcTemplate jdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public CustomerAuthentication loadUserByUserIdx(Long userId) {
        String query = "SELECT customerIdx, uid, role FROM Customers\n" +
                "WHERE customerIdx = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new CustomerAuthentication(
                        Long.valueOf(rs.getInt("customerIdx")),
                        rs.getString("uid"),
                        Collections.singletonList(rs.getString("role"))
                ), userId);
    }

    /**
     * CustomerDao - 1
     * 23.07.20 작성자 : 정주현
     * 회원가입 시 ID 중복 SELECT 쿼리
     */

    public int checkUid(String uid) {

        String query = "SELECT EXISTS(SELECT customerIdx FROM Customers WHERE uid = ? and status = 'A')";

        return this.jdbcTemplate.queryForObject(query, int.class, uid);
    }

    /**
     * CustomerDao - 2
     * 23.07.20 작성자 : 정주현
     * 회원가입 INSERT 쿼리
     */
    @Transactional
    public int signup(PostSignUpReq postSignUpReq, String salt) {
        String query = "INSERT INTO Customers(name, birthday, phone, uid, salt, password, email, service_check, personal_check, info_service_check, sms_check, email_check, call_check, role)\n" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object[] params = new Object[]{
                postSignUpReq.getName(),
                postSignUpReq.getBirthday(),
                postSignUpReq.getPhone(),
                postSignUpReq.getUid(),
                salt,
                postSignUpReq.getPassword(),
                postSignUpReq.getEmail(),
                postSignUpReq.getServiceCheck(),
                postSignUpReq.getPersonalCheck(),
                postSignUpReq.getInfo_service_check(),
                postSignUpReq.getSmsCheck(),
                postSignUpReq.getEmailCheck(),
                postSignUpReq.getCallCheck(),
                "ROLE_CUSTOMER"
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }


    /**
     * CustomerDao - 3
     * 23.07.20 작성자 : 정주현
     * 회원가입 정보 조회 Select 쿼리
     */

    public PostSignUpRes signUpComplete(int customerIdx) {
        String query = "SELECT *\n" +
                       "FROM Customers\n" +
                       "WHERE customerIdx=? ";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PostSignUpRes(
                        rs.getString("name"),
                        rs.getString("birthday"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("created")
                ), customerIdx);
    }


    public Customer login(PostLoginReq postLoginReq) {
        String query = "SELECT * FROM Customers WHERE uid = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new Customer(
                        rs.getInt("customerIdx"),
                        rs.getString("name"),
                        rs.getString("birthday"),
                        rs.getString("phone"),
                        rs.getString("uid"),
                        rs.getString("salt"),
                        rs.getString("password"),
                        rs.getString("email")
                ), postLoginReq.getUid());
    }

    public int userAuthy(PostSignUpAuthyReq signUpAuthy) {
        String query = "SELECT NOT EXISTS(\n" +
                "    SELECT * FROM Customers\n" +
                "             WHERE name = ? AND\n" +
                "                   birthday = ? AND\n" +
                "                   phone = ? AND \n" +
                "                   status = 'A'\n" +
                ")";
        Object[] params = new Object[]{
                signUpAuthy.getName(),
                signUpAuthy.getBirth(),
                signUpAuthy.getPhoneNum()
        };

        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    public int validLoginInfo(SmsCertificateReq smsCertificateReq, String findType){
        String query = "SELECT EXISTS(" +
                "SELECT * FROM Customers C " +
                (findType.equals("IC") ? "WHERE C.name = ? AND C.phone = ?)" : "WHERE C.uid = ? AND C.phone = ?)");

        Object[] params = new Object[]{
                (findType.equals("IC") ?smsCertificateReq.getName() : smsCertificateReq.getUid()),
                smsCertificateReq.getPhoneNum()
        };
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    public List<UidRecovRes> idFind(UidRecovReq receivedNumConfReq) {
        String query =
                "SELECT\n" +
                "   uid, DATE_FORMAT(created, '%Y.%m.%d.') as signUpDate\n" +
                "FROM Customers\n" +
                "WHERE phone = ?\n" +
                "AND name = ?;";
        Object[] params = new Object[]{
                receivedNumConfReq.getPhoneNum(),
                receivedNumConfReq.getName()
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new UidRecovRes(
                        rs.getString("uid"),
                        rs.getString("signUpDate")
                ),
                params);

    }

    public int JwtForRestorePw(PwRecovReq receivedNumConfReq) {
        String query = "SELECT customerIdx FROM Customers\n" +
                "WHERE uid = ? AND phone = ?";

        Object[] params = new Object[]{
                receivedNumConfReq.getUid(),
                receivedNumConfReq.getPhoneNum()
        };

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("customerIdx")
                , params);
    }

    public void pwRestore(int customerIdx, String salt, String pwd) {
        String query = "UPDATE Customers\n" +
                "SET salt = ? , password = ?\n" +
                "WHERE customerIdx = ?";

        Object[] params = new Object[]{
                salt,
                pwd,
                customerIdx
        };

        this.jdbcTemplate.update(query, params);
    }

    //닉네임 조회
    public GetNicknameRes nicknameSearch(int customerIdx) {
        String query = "SELECT nickname, profileImageUrl from Customers where customerIdx =? ";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetNicknameRes(
                        rs.getString("nickname"),
                        rs.getString("profileImageUrl")
                ), customerIdx);
    }

    //마이 떨이 조회
    public GetMyInfoRes myInfoSearch(int customerIdx){
        String query = "SELECT nickname, profileImage, email, phone from Customers WHERE customerIdx =?";

        return this.jdbcTemplate.queryForObject(query,
                (rs,rowNum) -> new GetMyInfoRes(
                        rs.getString("nickname"),
                        rs.getString("profileImageUrl"),
                        rs.getString("email"),
                        rs.getString("phone")
                ),customerIdx);
    }
}
