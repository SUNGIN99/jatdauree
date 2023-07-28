package com.example.jatdauree.src.domain.app.basket.dao;

import com.example.jatdauree.src.domain.app.basket.dto.BasketExist;
import com.example.jatdauree.src.domain.app.basket.dto.BasketItem;
import com.example.jatdauree.src.domain.app.basket.dto.BasketItemFromDao;
import com.example.jatdauree.src.domain.app.basket.dto.PostBasketReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BasketDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<BasketExist> checkBaketAlreadyExists(int customerIdx) {
        String query = "SELECT * FROM Basket WHERE customerIdx = ? AND status = 'A'";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new BasketExist(
                        rs.getInt("basketIdx"),
                        rs.getInt("storeIdx"),
                        rs.getInt("todaymenuIdx"),
                        rs.getInt("remain"),
                        rs.getString("status")
                ), customerIdx);
    }

    public int postBasket(int customerIdx, PostBasketReq basketReq) {
        String query = "INSERT INTO Basket(storeIdx, customerIdx, todaymenuIdx, cnt)\n" +
                "VALUES(?, ?, ?, ?)";
        Object[] params = new Object[]{
                basketReq.getStoreIdx(),
                customerIdx,
                basketReq.getTodaymenuIdx(),
                basketReq.getCount()
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public void renewBasket(int customerIdx) {
        String query = "UPDATE Basket\n" +
                "    SET status = 'D'\n" +
                "WHERE customerIdx = ? \n" +
                "AND status <> 'D'";

        this.jdbcTemplate.update(query, customerIdx);
    }

    public List<BasketItemFromDao> getBasket(int customerIdx) {
        String query ="" +
                "SELECT\n" +
                "    B.storeIdx, store_name,\n" +
                "    B.todaymenuIdx, B.cnt,\n" +
                "    M.price as originPrice,\n" +
                "    M.menu_url, M.menu_name,\n" +
                "    TM.price as discountedPrice,\n" +
                "    TM.discount\n" +
                "FROM Basket B\n" +
                "LEFT JOIN TodayMenu TM on B.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "LEFT JOIN Stores S on B.storeIdx = S.storeIdx\n" +
                "WHERE customerIdx = ?\n" +
                "    AND B.status = 'A'\n" +
                "    AND TM.status = 'A'\n" +
                "    AND M.status = 'A'\n" +
                "    AND S.status = 'A'";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new BasketItemFromDao(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getInt("todaymenuIdx"),
                        rs.getString("menu_url"),
                        rs.getString("menu_name"),
                        rs.getInt("cnt"),
                        rs.getInt("originPrice"),
                        rs.getInt("discount"),
                        rs.getInt("discountedPrice")
                ), customerIdx);
    }

    public int getBasketCount(int customerIdx) {
        String query = "SELECT COUNT(basketIdx) as basketCount FROM Basket\n" +
                "WHERE customerIdx = ? \n" +
                "  AND status = 'A' ";

        return this.jdbcTemplate.queryForObject(query, int.class, customerIdx);
    }
}
