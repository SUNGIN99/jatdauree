package com.example.jatdauree.src.domain.menu.dao;

import com.example.jatdauree.src.domain.menu.dto.PostIngredientReq;
import com.example.jatdauree.src.domain.menu.dto.PostMenuReq;
import com.example.jatdauree.src.domain.menu.dto.PostMenuUpReq;
import com.example.jatdauree.src.domain.menu.dto.PostStatusUpReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;


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
     * 23.07.06 작성자 : 이윤채
     * menuRegiste 메뉴등록
     */
    @Transactional
    public int menuRegisterDao(PostMenuReq postMenuReq)  {
        String query = "INSERT INTO Menu (storeIdx,menu_name,price,composition,description,menu_url,status)\n" +
                "VALUE (?,?,?,?,?,?,?);";
        Object[] params = new Object[]{
                postMenuReq.getStoreIdx(),
                postMenuReq.getMenuName(),
                postMenuReq.getPrice(),
                postMenuReq.getComposition(),
                postMenuReq.getDescription(),
                postMenuReq.getMenuUrl(),
                postMenuReq.getStatus(),
        };
            this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "SELECT LAST_INSERT_ID();";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    //원산지
    /**
     * menuDao - 2
     * 23.07.06 작성자 : 이윤채
     * ingredientDao 원산지등록
     */

    public int ingredientDao(PostIngredientReq postIngredientReq){
        String query ="INSERT INTO Ingredients (storeIdx, ingredient_name, origin, menu_names, status)\n" +
                "VALUES (?,?,?,?,?);";
        Object[] params = new Object[]{
                postIngredientReq.getStoreIdx(),
                postIngredientReq.getIngredientName(),
                postIngredientReq.getOrigin(),
                postIngredientReq.getMenuName(),
                postIngredientReq.getStatus()
        };
        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "SELECT LAST_INSERT_ID();";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);

    }

    //원산지 등록 후 판매자 테이블의 최초로그인 상태와 가게등록 상태 값이 0,0이 되게끔 update
    //String query = "UPDATE Merchandisers SET store_register = 0, first_login = 0 WHERE sellerIdx = ?";

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



