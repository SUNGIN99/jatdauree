package com.example.jatdauree.src.domain.menu.dao;

import com.example.jatdauree.src.domain.menu.dto.*;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //메뉴등록
    /**
     * menuDao - 1
     * 23.07.07 작성자 : 이윤채, 김성인
     * Main menuRegister 메인 메뉴등록
     * 반환 정보 : 메인 메뉴 등록 개수
     */
    public int mainMenuRegister(int storeIdx, ArrayList<MainMenuItem> mainMenuList)  {
        String query = "INSERT INTO Menu (" +
                "storeIdx, " +
                "menu_name, " +
                "price, " +
                "composition," +
                "description," +
                "menu_url," +
                "status)\n" +
                "VALUES (?,?,?,?,?,?,'M');";

            this.jdbcTemplate.batchUpdate(query,
                    mainMenuList,
                    mainMenuList.size(),
                    (PreparedStatement ps, MainMenuItem menuItem) ->{
                        ps.setInt(1, storeIdx);
                        ps.setString(2, menuItem.getMenuName());
                        ps.setInt(3, menuItem.getPrice());
                        ps.setString(4, menuItem.getComposition());
                        ps.setString(5, menuItem.getDescription());
                        ps.setString(6, menuItem.getMenuUrl());
                    });

        return mainMenuList.size();
    }

    /**
     * menuDao - 2
     * 23.07.07 작성자 : 이윤채, 김성인
     * Main menuRegister 사이드 메뉴등록
     * 반환 정보 : 사이드 메뉴 등록 개수
     */
    public int sideMenuRegister(int storeIdx, ArrayList<SideMenuItem> sideMenuList)  {
        String query = "INSERT INTO Menu (" +
                "storeIdx, " +
                "menu_name, " +
                "price, " +
                "composition," +
                "description," +
                "menu_url," +
                "status)\n" +
                "VALUES (?,?,?,?,?,?,'S');";

        this.jdbcTemplate.batchUpdate(query,
                sideMenuList,
                sideMenuList.size(),
                (PreparedStatement ps, SideMenuItem menuItem) ->{
                    ps.setInt(1, storeIdx);
                    ps.setString(2, menuItem.getMenuName());
                    ps.setInt(3, menuItem.getPrice());
                    ps.setString(4, menuItem.getComposition());
                    ps.setString(5, menuItem.getDescription());
                    ps.setString(6, menuItem.getMenuUrl());
                });

        return sideMenuList.size();
    }

    /**
     * menuDao - 3
     * 23.07.07 작성자 : 김성인
     * 원산지 등록
     * 반환 정보 : 원산지 등록 개수
     */
    public int ingredientRegister(int storeIdx, ArrayList<IngredientItem> ingredientList)  {
        String query ="INSERT INTO Ingredients (storeIdx, ingredient_name, origin, menu_names)\n" +
                "VALUES (?,?,?,?);";

        this.jdbcTemplate.batchUpdate(query,
                ingredientList,
                ingredientList.size(),
                (PreparedStatement ps, IngredientItem ingredientItem) ->{
                    ps.setInt(1, storeIdx);
                    ps.setString(2, ingredientItem.getIngredientName());
                    ps.setString(3, ingredientItem.getOrigin());
                    ps.setString(4, ingredientItem.getMenuName());
                });

        return ingredientList.size();
    }

    /**
     * MenuDao - 2
     * 23.07.04 작성자 : 정주현, 김성인
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

    /**
     * menuDao - 3
     * 23.07.06 작성자 : 이윤채
     * statusChangeDao 원산지까지 입력을 하면 first_login 상태가 0이 되도록
     */
    public int statusChangeDao(PostStatusUpReq postStatusUpReq){
        String query = "UPDATE Merchandisers SET  first_login = 0 WHERE sellerIdx = ?";
        Object[] params = new Object[]{

                postStatusUpReq.getSellerIdx()

        };
        return this.jdbcTemplate.update(query, params);

        //String lastInsertIdQuery = "SELECT LAST_INSERT_ID();";
        //return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }
    /**
     * menuDao - 4
     * 23.07.06 작성자 : 이윤채
     * menuUpdate 메뉴 업데이트 (POST)
     */
    public int menuUpdate(PostMenuUpReq postMenuUpReq){
        String query= "UPDATE Menu SET menu_name = ?, price = ? ,composition=?,description=?,menu_url=?,status=? WHERE menuIdx = ? AND storeIdx=?;";
        Object[] params = new Object[]{

                postMenuUpReq.getMenuName(),
                postMenuUpReq.getPrice(),
                postMenuUpReq.getComposition(),
                postMenuUpReq.getDescription(),
                postMenuUpReq.getMenuUrl(),
                postMenuUpReq.getStatus(),
                postMenuUpReq.getMenuIdx(),
                postMenuUpReq.getStoreIdx()

        };
        return this.jdbcTemplate.update(query, params);
    }


}



