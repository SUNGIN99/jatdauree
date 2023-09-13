package com.umc.jatdauree.src.domain.app.subscribe.dao;
import com.umc.jatdauree.src.domain.app.subscribe.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppSubscribeDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //subIdx 찾기
    public int sudIdxByCustomerIdxStoreIdx(int customerIdx, int storeIdx) {
        String query = "SELECT subIdx FROM Subscribe WHERE customerIdx = ? AND storeIdx=?;";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("subIdx"), customerIdx, storeIdx);
    }

    //첫구독
    public int firstSubscribe(int customerIdx, int storeIdx) {
        String query = "INSERT INTO Subscribe(customerIdx,storeIdx,status) VALUE (?,?,'A');";
        Object[] params = new Object[]{customerIdx, storeIdx};
        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    //구독 테이블에 구독정보 있는지 확인
    public int checkSubscribe(int customerIdx, int storeIdx) {
        String query = "SELECT EXISTS (SELECT * FROM Subscribe WHERE customerIdx=? AND storeIdx=?);";

        Object[] params = new Object[]{customerIdx, storeIdx};
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    public int checkSubscribeValid(int customerIdx, int storeIdx) {
        String query = "SELECT EXISTS (SELECT * FROM Subscribe WHERE customerIdx=? AND storeIdx=? AND status = 'A');";

        Object[] params = new Object[]{customerIdx, storeIdx};
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    //구독상태 확인
    public String checkSubscribeStatus(int customerIdx, int storeIdx) {
        String query = "SELECT status FROM Subscribe WHERE customerIdx=? AND storeIdx=?;";
        return this.jdbcTemplate.queryForObject(query, String.class, customerIdx, storeIdx);
    }

    //다시 구독
    public int reSubscribe(int customerIdx, int storeIdx) {
        String query = "UPDATE Subscribe SET status = 'A' WHERE customerIdx =? AND storeIdx =?;";
        return jdbcTemplate.update(query, customerIdx, storeIdx);
    }

    //구독취소
    public int cancelSubscription(int customerIdx, int storeIdx) {
        String query = "UPDATE Subscribe SET status = 'D' WHERE customerIdx =? AND storeIdx =?;";
        return jdbcTemplate.update(query, customerIdx, storeIdx);
    }

    //가게존재여부
    public int existStore(int storeIdx){
        String query ="SELECT EXISTS(SELECT storeIdx FROM Stores WHERE storeIdx =?);";
        return jdbcTemplate.queryForObject(query, Integer.class,storeIdx);
    }


    public List<GetAppSubscriptionRes> getSubscriptionList(int customerIdx) {
        String query = "SELECT\n" +
                "    S.storeIdx,\n" +
                "    S.store_name,\n" +
                "    S.store_logo_url,\n" +
                "    S.sign_url,\n" +
                "    S.x, S.y,\n" +
                "    ROUND(AVG(R.star), 1) AS star_average\n" +
                "FROM Subscribe SUB\n" +
                "LEFT JOIN Stores S on SUB.storeIdx = S.storeIdx\n" +
                "LEFT JOIN Review R on S.storeIdx = R.storeIdx AND R.status != 'D'\n" +
                "WHERE SUB.customerIdx = ?\n" +
                "AND SUB.status = 'A'\n" +
                "GROUP BY SUB.storeIdx";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetAppSubscriptionRes(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("store_logo_url"),
                        rs.getString("sign_url"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        0, 0,
                        rs.getDouble("star_average"),
                        1

                ), customerIdx);
    }


}
