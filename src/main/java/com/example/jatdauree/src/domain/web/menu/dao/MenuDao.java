package com.example.jatdauree.src.domain.web.menu.dao;

import com.example.jatdauree.src.domain.web.menu.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
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
    public int menuRegister(int storeIdx, List<PostMenuUrlItem> mainMenuList, String mOrS)  {
        String query = "INSERT INTO Menu (" +
                "storeIdx, " +
                "menu_name, " +
                "price, " +
                "composition," +
                "description," +
                "menu_url," +
                "status)\n" +
                "VALUES (?,?,?,?,?,?,?);";

            this.jdbcTemplate.batchUpdate(query,
                    mainMenuList,
                    mainMenuList.size(),
                    (PreparedStatement ps, PostMenuUrlItem menuItem) ->{
                        ps.setInt(1, storeIdx);
                        ps.setString(2, menuItem.getMenuName());
                        ps.setInt(3, menuItem.getPrice());
                        ps.setString(4, menuItem.getComposition());
                        ps.setString(5, menuItem.getDescription());
                        ps.setString(6, menuItem.getMenuUrl());
                        ps.setString(7, mOrS);
                    });

        return mainMenuList.size();
    }

    /**
     * menuDao - 3
     * 23.07.07 작성자 : 김성인
     * 원산지 등록
     * 반환 정보 : 원산지 등록 개수
     */
    public int ingredientRegister(int storeIdx, List<PostIngredientItem> ingredientList)  {
        String query ="INSERT INTO Ingredients (storeIdx, ingredient_name, origin, menu_names)\n" +
                "VALUES (?,?,?,?);";

        this.jdbcTemplate.batchUpdate(query,
                ingredientList,
                ingredientList.size(),
                (PreparedStatement ps, PostIngredientItem postIngredientItem) ->{
                    ps.setInt(1, storeIdx);
                    ps.setString(2, postIngredientItem.getIngredientName());
                    ps.setString(3, postIngredientItem.getOrigin());
                    ps.setString(4, postIngredientItem.getMenuName());
                });

        return ingredientList.size();
    }

    /**
     * MenuDao - 4
     * 23.07.04 작성자 : 정주현, 김성인
     * 가게 idx로 가게에 등록된 메뉴 조회
     */
    public List<GetMenuItem> getStoreMenuList(int storeIdx, String status) {
        String query = "SELECT menuIdx, menu_name, price, composition, description, menu_url\n" +
                "FROM Menu WHERE storeIdx = ? AND status = ?";

        Object[] params = new Object[]{storeIdx, status};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetMenuItem(
                        rs.getInt("menuIdx"),
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getString("composition"),
                        rs.getString("description"),
                        rs.getString("menu_url"),
                        0
                ), params);
    }

    /**
     * menuDao - 4
     * 23.07.06 작성자 : 이윤채
     * menuUpdate 메뉴 업데이트 (POST)
     */
    public int menuUpdate(List<PatchMenuUrlItem> menuItems){
        String query= "UPDATE Menu\n" +
                "SET menu_name = ?,\n" +
                "    price = ?,\n" +
                "    composition = ?,\n" +
                "    description = ?, \n" +
                "    menu_url = ? \n" +
                "WHERE menuIdx = ?";

        return this.jdbcTemplate.batchUpdate(query,
                menuItems,
                menuItems.size(),
                (PreparedStatement ps, PatchMenuUrlItem item) ->{
                    ps.setString(1, item.getMenuName());
                    ps.setInt(2, item.getPrice());
                    ps.setString(3, item.getComposition());
                    ps.setString(4, item.getDescription());
                    ps.setString(5, item.getMenuUrl());
                    ps.setInt(6, item.getMenuIdx());
                }
        ).length;
    }

    public int menuDeActive(List<PatchMenuItem> menuItems){
        String query = "UPDATE Menu\n" +
                "SET status = 'D'\n" +
                "WHERE menuIdx = ?";

        return this.jdbcTemplate.batchUpdate(query,
                menuItems,
                menuItems.size(),
                (PreparedStatement ps, PatchMenuItem item) ->{
                    ps.setInt(1, item.getMenuIdx());
                }
        ).length;
    }


}



