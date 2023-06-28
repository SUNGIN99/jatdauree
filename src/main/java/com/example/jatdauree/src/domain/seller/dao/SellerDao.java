package com.example.jatdauree.src.domain.seller.dao;

import com.example.jatdauree.src.domain.seller.dto.PostLoginReq;
import com.example.jatdauree.src.domain.seller.dto.PostSignUpReq;
import com.example.jatdauree.src.domain.seller.dto.PostSignUpRes;
import com.example.jatdauree.src.domain.seller.dto.Seller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;


@Repository
public class SellerDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * sellerDao - 1
     * 23.06.29 작성자 : 김성인
     * 회원가입 시 ID 중복 SELECT 쿼리
     */
    public int checkUid(String uid) {
        String query = "SELECT EXISTS(SELECT sellerIdx FROM Merchandisers WHERE uid = ? and status = 'A')";
        return this.jdbcTemplate.queryForObject(query, int.class, uid);
    }

    /**
     * sellerDao - 2
     * 23.06.29 작성자 : 김성인
     * 회원가입 INSERT 쿼리
     */
    @Transactional
    public int signUp(PostSignUpReq postSignUpReq, String salt){
        String query = "INSERT INTO Merchandisers(name, birthday, phone, uid, salt, password, email, first_login, service_check, personal_check, sms_check, email_check, call_check)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] params = new Object[]{
                postSignUpReq.getName(),
                postSignUpReq.getBirthday(),
                postSignUpReq.getPhone(),
                postSignUpReq.getUid(),
                salt,
                postSignUpReq.getPassword(),
                postSignUpReq.getEmail(),
                1,
                postSignUpReq.getServiceCheck(),
                postSignUpReq.getPersonalCheck(),
                postSignUpReq.getSmsCheck(),
                postSignUpReq.getEmailCheck(),
                postSignUpReq.getCallCheck()
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    /**
     * sellerDao - 3
     * 23.06.29 작성자 : 김성인
     * 회원가입 정보 조회 Select 쿼리
     */
    public PostSignUpRes signUpComplete(int sellerIdx) {
        String query = "SELECT * FROM Merchandisers WHERE sellerIdx = ?";
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
                ), sellerIdx);
    }

    /**
     * sellerDao - 4
     * 23.06.29 작성자 : 김성인
     * 로그인 접근 시 ID 기준 유저 존재 여부 확인
     */
    public Seller login(PostLoginReq postLoginReq) {
        String query = "SELECT * FROM Merchandisers WHERE uid = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new Seller(
                        rs.getInt("sellerIdx"),
                        rs.getString("name"),
                        rs.getString("birthday"),
                        rs.getString("phone"),
                        rs.getString("uid"),
                        rs.getString("salt"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getInt("first_login")
                ), postLoginReq.getUid());
    }
}
