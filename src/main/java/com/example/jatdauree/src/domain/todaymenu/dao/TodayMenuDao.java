package com.example.jatdauree.src.domain.todaymenu.dao;

import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;

@Repository
public class TodayMenuDao {

    private JdbcTemplate jdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * TodayMenuDao - 1
     * 23.07.08 작성자 : 정주현, 김성인
     * 떨이상품 등록 INSERT 쿼리
     */
    public int registerTodayMenu(int storeIdx, ArrayList<PostTodayMenuItem> todayMenuItems, String status) {
        String query = "INSERT INTO TodayMenu(date, storeIdx, menuIdx, price, discount, remain, status)\n" +
                "VALUE(NOW(),?,?,?,?,?,?)";

        this.jdbcTemplate.batchUpdate(query,
                todayMenuItems,
                todayMenuItems.size(),
                (PreparedStatement ps, PostTodayMenuItem todayMenuItem) ->{
                    ps.setInt(1, storeIdx);
                    ps.setInt(2, todayMenuItem.getMenuIdx());
                    ps.setInt(3, todayMenuItem.getDiscountPrice());
                    ps.setInt(4, todayMenuItem.getDiscount());
                    ps.setInt(5, todayMenuItem.getRemain());
                    ps.setString(6, status);
                });

        return todayMenuItems.size();
    }

}












