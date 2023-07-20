package com.example.jatdauree.src.domain.app.customer.dao;


import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpReq;
import com.example.jatdauree.src.domain.app.customer.dto.PostSignUpRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CustomerDao {

    private JdbcTemplate jdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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

    public  int signup(PostSignUpReq postSignUpReq) {

        String query = "INSERT INTO Customers(name, birthday, phone, uid, password, email, service_check, personal_check, info_service_check, sms_check, email_check, call_check, role)\n" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Object params = new Object[]{
                postSignUpReq.getName(),
                postSignUpReq.getBirthday(),
                postSignUpReq.getPhone(),
                postSignUpReq.getUid(),
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
                        rs.getString("uid"),
                        rs.getString("name"),
                        rs.getString("birthday"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("created"),
                        rs.getInt("sms_check"),
                        rs.getInt("email_check"),
                        rs.getInt("call_check")
                ), customerIdx);
    }
}
