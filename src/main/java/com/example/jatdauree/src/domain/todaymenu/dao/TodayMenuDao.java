package com.example.jatdauree.src.domain.todaymenu.dao;

import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuListItem;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegReq;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TodayMenuDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * TodayMenuDao - 1
     * 23.07.05 작성자 : 정주현
     * 떨이상품 등록 INSERT 쿼리
     */

    public int RegisterTodayMenu(PostTodayMenuRegReq postTodayMenuRegReq) {
        String query = "INSERT INTO TodayMenu(date, storeIdx, menuIdx, price, discount, remain)\n" +
                "VALUE(?,?,?,?,?,?)";

        for (PostTodayMenuListItem item : postTodayMenuRegReq.getTodayMenuListItems()) {
            jdbcTemplate.update(query,postTodayMenuRegReq.getDate(), postTodayMenuRegReq.getStoreIdx(), item.getMenuIdx(),
                    item.getDiscountPrice(), item.getDiscount(), item.getRemain());
        }//서비스

        String lastInsertIdQuery = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

    }

    /**
     * TodayMenuDao - 2
     * 23.07.06 작성자 : 정주현
     * 떨이상품 등록 후 todayMenutable 조회 (SELECT)
     */


    public ArrayList<PostTodayMenuRegRes> searchTodayMenuTable(int todaymenuIdx){
        String query = "SELECT * FROM TodayMenu WHERE (date,storeIdx) IN ( SELECT date,storeIdx FROM TodayMenu WHERE todaymenuIdx = ?)";

        List<PostTodayMenuRegRes> postTodayMenuRegResList = this.jdbcTemplate.query(query,
                (rs, rowNum) -> new PostTodayMenuRegRes(
                        rs.getLong("todaymenuIdx"),
                        rs.getString("date"),
                        rs.getLong("storeIdx"),
                        rs.getLong("menuIdx"),
                        rs.getInt("price"),
                        rs.getInt("discount"),
                        rs.getInt("remain")
                ), todaymenuIdx);

        return new ArrayList<>(postTodayMenuRegResList);
    }

}












