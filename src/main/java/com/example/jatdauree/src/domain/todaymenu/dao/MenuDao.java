package com.example.jatdauree.src.domain.todaymenu.dao;

import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuDao {

    private JdbcTemplate jdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * MenuDao - 1
     * 23.07.04 작성자 : 정주현
     * 가게 idx로 가게에 등록된 메뉴 조회
     */

    public ArrayList<GetMenusSearchRes> searchMenu(int storeIdx) {
        String query = "SELECT menuIdx,storeIdx,menu_name,price,status\n" +
                "FROM Menu WHERE storeIdx = ?";

        List<GetMenusSearchRes> orderList = this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetMenusSearchRes(
                        rs.getInt("menuIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getString("status")
                ), storeIdx);

        return new ArrayList<>(orderList);
    }

    public int getStroeIdxbySellerIdx(int sellerIdx) {
        String query = "SELECT storeIdx FROM Stores WHERE sellerIdx = ?";

        return this.jdbcTemplate.queryForObject(query, int.class, sellerIdx);
    }
}




