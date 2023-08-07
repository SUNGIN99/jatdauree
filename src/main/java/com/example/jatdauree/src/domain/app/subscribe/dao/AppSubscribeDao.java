package com.example.jatdauree.src.domain.app.subscribe.dao;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.store.dto.GetAppStore;
import com.example.jatdauree.src.domain.app.subscribe.dto.*;
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
        return this.jdbcTemplate.update(query, params);
    }

    //구독 테이블에 구독정보 있는지 확인
    public int checkSubscribe(int customerIdx, int storeIdx) {
        String query = "SELECT EXISTS (SELECT * FROM Subscribe WHERE customerIdx=? AND storeIdx=?);";

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
    public int ExistStore(int storeIdx){
        String query ="SELECT EXISTS(SELECT storeIdx FROM Stores WHERE storeIdx =?);";
        return jdbcTemplate.queryForObject(query, Integer.class,storeIdx);
    }


    public List<GetAppSubscriptionRes> getSubscriptionList(int customerIdx) {
        String query = "SELECT S.storeIdx, S.store_logo_url, S.sign_url, S.categoryIdx, S.store_name, S.x, S.y, R.star_average, SUB.status AS sub_Status "
                + "FROM Stores AS S "
                + "LEFT JOIN (SELECT storeIdx, ROUND(AVG(star), 1) AS star_average FROM Review WHERE status <> 'D' GROUP BY storeIdx) AS R "
                + "ON S.storeIdx = R.storeIdx "
                + "LEFT JOIN Subscribe AS SUB ON S.storeIdx = SUB.storeIdx AND SUB.customerIdx = ? "
                + "WHERE SUB.status ='A';";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetAppSubscriptionRes(
                        rs.getInt("storeIdx"),
                        rs.getString("store_logo_url"),
                        rs.getString("sign_url"),
                        rs.getInt("categoryIdx"),
                        rs.getString("store_name"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("star_average"),
                        rs.getString("sub_Status")

                ), customerIdx);
    }


}
