package com.example.jatdauree.src.domain.app.basket.dao;

import com.example.jatdauree.src.domain.app.basket.dto.*;
import com.example.jatdauree.src.domain.web.todaymenu.dto.GetMainPageItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
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
                        rs.getInt("cnt"),
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
                "    B.todaymenuIdx, B.basketIdx, B.cnt,\n" +
                "    M.price as originPrice,\n" +
                "    M.menu_url, M.menu_name,\n" +
                "    TM.price as discountedPrice,\n" +
                "    TM.discount\n" +
                "FROM Basket B\n" +
                "LEFT JOIN TodayMenu TM on B.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx AND M.status != 'D'\n" +
                "LEFT JOIN Stores S on B.storeIdx = S.storeIdx AND S.status = 'A'\n" +
                "WHERE customerIdx = ?\n" +
                "    AND B.status = 'A'";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new BasketItemFromDao(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getInt("todaymenuIdx"),
                        rs.getInt("basketIdx"),
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

    public BasketOrderRes getBasketOrder(int userIdx) {
        String query = "SELECT\n" +
                "    S.storeIdx,\n" +
                "    S.store_name,\n" +
                "    S.store_address,\n" +
                "    C.phone,\n" +
                "    SUM(B.cnt * TM.price) as orderPrice\n" +
                "FROM Basket B\n" +
                "LEFT JOIN TodayMenu TM on B.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Stores S on B.storeIdx = S.storeIdx\n" +
                "LEFT JOIN Customers C on B.customerIdx = C.customerIdx\n" +
                "WHERE B.customerIdx = ?\n" +
                "AND B.status != 'D'";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new BasketOrderRes(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("store_address"),
                        rs.getString("phone"),
                        rs.getInt("orderPrice")
                ), userIdx);
    }

    public int postBasketOrder(int userIdx, OrderDoneReq orderReq) {
        String query = "INSERT INTO Orders(storeIdx, customerIdx, request, order_time, pickup_time, payment_status)\n" +
                "VALUES(?, ?, ?, NOW(), ?, ?)";

        Object[] params = new Object[]{
                orderReq.getStoreIdx(),
                userIdx,
                orderReq.getRequest(),
                orderReq.getPickupTime(),
                orderReq.getPaymentStatus()
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public List<BasketOrderItem> getBasketOrderItems(int userIdx) {
        String query = "SELECT * FROM Basket WHERE customerIdx = ? AND status = 'A'";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new BasketOrderItem(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5)
                ), userIdx);

    }

    public BasketTodayMenu checkItemRemain(int storeIdx, int todayMenuIdx, int cnt) {
        String query = "SELECT\n" +
                "    *\n" +
                "FROM TodayMenu\n" +
                "WHERE storeIdx = ?\n" +
                "AND todaymenuIdx = ?\n" +
                "AND remain >= ?";

        Object[] params = new Object[]{
                storeIdx,
                todayMenuIdx,
                cnt
        };

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new BasketTodayMenu(
                        rs.getInt("todaymenuIdx"),
                        rs.getInt("remain")
                ),params);
    }

    public int postBasketOrderItems(int orderIdx, List<BasketOrderItem> basketItems) {
        String query = "INSERT INTO OrderLists(orderIdx, todaymenuIdx, cnt)\n" +
                "VALUES(?, ?, ?)";

        return this.jdbcTemplate.batchUpdate(query,
                basketItems,
                basketItems.size(),
                (PreparedStatement ps, BasketOrderItem bItem) ->{
                    ps.setInt(1, orderIdx);
                    ps.setInt(2, bItem.getTodayMenuIdx());
                    ps.setInt(3, bItem.getCnt());
                }).length;

    }

    public int todayMenuDecrease(List<BasketOrderItem> basketItems) {
        String query = "UPDATE TodayMenu\n" +
                "    SET remain = remain - ?\n" +
                "WHERE todaymenuIdx = ?\n" +
                "AND remain >= ?";

        return this.jdbcTemplate.batchUpdate(query,
                basketItems,
                basketItems.size(),
                (PreparedStatement ps, BasketOrderItem bItem) ->{
                    ps.setInt(1, bItem.getCnt());
                    ps.setInt(2, bItem.getTodayMenuIdx());
                    ps.setInt(3, bItem.getCnt());
                }).length;

    }


    public int basketItemDone(List<BasketOrderItem> basketItems) {
        String query = "UPDATE Basket\n" +
                "    SET status = 'D'\n" +
                "WHERE basketIdx = ?";

        return this.jdbcTemplate.batchUpdate(query,
                basketItems,
                basketItems.size(),
                (PreparedStatement ps, BasketOrderItem bItem) ->{
                    ps.setInt(1, bItem.getBasketIdx());
                }).length;
    }

    public String getStoreUrl(int storeIdx) {
        String query = "SELECT\n" +
                "    store_logo_url\n" +
                "FROM Stores\n" +
                "WHERE storeIdx = ?";

        return this.jdbcTemplate.queryForObject(query, String.class, storeIdx);

    }

    public void patchBasketCount(int basketIdx, int inDecrease) {
        String query = "UPDATE Basket \n" +
                "SET cnt = cnt + ?\n" +
                "WHERE basketIdx = ?";

        Object[] params = new Object[]{
                inDecrease == 1 ? 1: -1,
                basketIdx
        };

        this.jdbcTemplate.update(query, params);
    }

    public void removeBasketItem(int basketIdx) {
        String query = "UPDATE Basket\n" +
                "SET status = 'D'\n" +
                "WHERE basketIdx = ?";

        this.jdbcTemplate.update(query, basketIdx);

    }
}
