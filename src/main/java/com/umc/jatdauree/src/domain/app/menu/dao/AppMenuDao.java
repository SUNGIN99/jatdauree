package com.umc.jatdauree.src.domain.app.menu.dao;

import com.umc.jatdauree.src.domain.app.menu.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class AppMenuDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //메뉴등록
    /**
     * menuDao - 1
     * 23.07.20 작성자 : 이윤채
     * 메뉴클릭시 정보 조회
     * 반환 정보 : GetAppMenuClickRes
     */
    public GetMenuDetailInfoRes getMenuDetailInfo(int todaymenuIdx){

        String query ="SELECT m.storeIdx, tm.todaymenuIdx, m.menu_url, m.menu_name, tm.remain,\n" +
                "    m.composition, m.description, m.price, tm.discount, tm.price AS price_today\n" +
                "FROM TodayMenu tm\n" +
                "JOIN Menu m ON m.menuIdx = tm.menuIdx\n" +
                "WHERE tm.todaymenuIdx = ?"; //조인할때는 투데이메뉴아이디엑스 기준

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetMenuDetailInfoRes(
                        rs.getInt("storeIdx"),
                        rs.getInt("todaymenuIdx"),
                        rs.getString("menu_url"),
                        rs.getString("menu_name"),
                        rs.getInt("remain"),
                        rs.getString("composition"),
                        rs.getString("description"),
                        rs.getInt("price"),
                        rs.getInt("discount"),
                        rs.getInt("price_today")

                ),todaymenuIdx);
    }




}
