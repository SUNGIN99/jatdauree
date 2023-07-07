package com.example.jatdauree.src.domain.seller.dao;

import com.example.jatdauree.src.domain.seller.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;


@Repository
public class SellerDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * sellerDao - 1
     * 23.07.03 작성자 : 김성인
     * JWT Role 추가하기 위한 유저 권한정보 등 조회
     */
    public SellerAuthentication loadUserByUserIdx(Long userId){
        String query = "SELECT sellerIdx, uid, role FROM Merchandisers\n" +
                "WHERE sellerIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new SellerAuthentication(
                        Long.valueOf(rs.getInt("sellerIdx")),
                        rs.getString("uid"),
                        Collections.singletonList(rs.getString("role"))
                ), userId);
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
        String query = "INSERT INTO Merchandisers(name, birthday, phone, uid, salt, password, email, first_login, service_check, personal_check, sms_check, email_check, call_check, role)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
                postSignUpReq.getCallCheck(),
                "ROLE_SELLER"
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
                        rs.getInt("first_login"),
                        rs.getInt("menu_register"),
                        null
                ), postLoginReq.getUid());
    }

    /**
     * sellerDao - 5
     * 23.07.02 작성자 : 김성인
     * 아이디 찾기할때 요청된 이름과 전화번호를 가진 사용자가 존재하는지?
     */
    public int validLoginInfo(SmsCertificateReq smsCertificateReq, String findType){
        String query = "SELECT EXISTS(" +
                "SELECT * FROM Merchandisers M " +
                (findType.equals("I") ? "WHERE M.name = ? AND M.phone = ?)" : "WHERE M.uid = ? AND M.phone = ?)");

        Object[] params = new Object[]{
                (findType.equals("I") ?smsCertificateReq.getName() : smsCertificateReq.getUid()),
                smsCertificateReq.getPhoneNum()
        };
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    /**
     * sellerDao - 6
     * 23.07.02 작성자 : 김성인
     * 인증번호 확인, 아이디 찾기 완료
     */
    public List<ReceivedNumConfRes> idFind(ReceivedNumConfReq receivedNumConfReq) {
        String query =
                "SELECT\n" +
                    "uid, DATE_FORMAT(created, '%Y.%m.%d.') as signUpDate\n" +
                "FROM Merchandisers \n" +
                        "WHERE phone = ? \n" +
                        "AND name = ?;";

        Object[] params = new Object[]{
                receivedNumConfReq.getPhoneNum(),
                receivedNumConfReq.getName()
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ReceivedNumConfRes(
                        rs.getString("uid"),
                        rs.getString("signUpDate")
                ),
                params);
    }

    /**
     * sellerDao - 7
     * 23.07.02 작성자 : 김성인
     * 비밀번호 찾기 전 인가된 접근을 위한 JWT발행 -> sellerIdx 반환
     */
    public int JwtForRestorePw(ReceivedNumConfReq receivedNumConfReq){
        String query = "SELECT sellerIdx FROM Merchandisers\n" +
                "WHERE uid = ? AND phone = ?";

        Object[] params = new Object[]{
                receivedNumConfReq.getUid(),
                receivedNumConfReq.getPhoneNum()
        };

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("sellerIdx")
                , params);
    }

    /**
     * sellerDao - 8
     * 23.07.02 작성자 : 김성인
     * 비밀번호 재설정
     */
    public void pwRestore(int sellerIdx, String salt, String pwd) {
        String query = "UPDATE Merchandisers\n" +
                "SET salt = ? , password = ?\n" +
                "WHERE sellerIdx = ?";

        Object[] params = new Object[]{
                salt,
                pwd,
                sellerIdx
        };

        this.jdbcTemplate.update(query, params);
    }

    /**
     * sellerDao - 9
     * 23.07.02 작성자 : 김성인
     * 판매자 메뉴 및 원산지 등록 완료
     */
    public void registerMenuNIngredient(int sellerIdx){
        String query = "UPDATE Merchandisers\n" +
                "SET menu_register = 0\n" +
                "WHERE sellerIdx = ?";
        this.jdbcTemplate.update(query, sellerIdx);
    }
}
