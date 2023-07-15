package com.example.jatdauree.src.domain.todaymenu.dao;

import com.example.jatdauree.src.domain.todaymenu.dto.GetMainPageItem;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
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

    public List<GetMainPageItem> getTodayMenuList(int storeIdx, String status) {
        String query = "SELECT\n" +
                "    M.menuIdx, M.menu_name, M.price as origin_price,\n" +
                "    IF(T.status = 'D' , null, T.todaymenuIdx) as todaymenuIdx,\n" +
                "    IF(T.status = 'D' , null, T.discount) as discount,\n" +
                "    IF(T.status = 'D' , null, T.price) as discount_price,\n" +
                "    IF(T.status = 'D' , null, T.remain) as remain,\n" +
                "    IF(T.status = 'D' , null, T.status) as status\n" +
                "FROM Menu M\n" +
                "LEFT JOIN TodayMenu T on M.menuIdx = T.menuIdx\n" +
                "WHERE M.storeIdx = ?\n" +
                "AND M.status = ?";

        Object[] params = new Object[]{
                storeIdx,
                status
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetMainPageItem(
                        rs.getInt("menuIdx"),
                        rs.getString("menu_name"),
                        rs.getInt("origin_price"),
                        rs.getInt("todaymenuIdx"),
                        rs.getInt("discount"),
                        rs.getInt("discount_price"),
                        rs.getInt("remain"),
                        rs.getString("status") == null ? "N" : rs.getString("status"),
                        rs.getString("status") == null ? -1 : 0
                ), params);
    }
}












