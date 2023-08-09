package com.example.jatdauree.src.domain.app.order.dao;

import com.example.jatdauree.src.domain.app.order.dto.GetOrderListRes;
import com.example.jatdauree.src.domain.app.order.dto.OrderDeleteReq;
import com.example.jatdauree.src.domain.app.order.dto.OrderDetailRes;
import com.example.jatdauree.src.domain.app.order.dto.OrderMenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AppOrderDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetOrderListRes> getOrderList(int customerIdx, String[] weekDays) {
        String query = "SELECT\n" +
                "    O.orderIdx,\n" +
                "    DATE_FORMAT(O.created, '%Y.%m.%d') as orderDate,\n" +
                "    DATE_FORMAT(O.created, '%w') as weekDay,\n" +
                "    S.storeIdx,\n" +
                "    S.store_name,\n" +
                "    M.menu_name,\n" +
                "    M.menu_url,\n" +
                "    TM.price,\n" +
                "    COUNT(OL.orderIdx) as orerItemCount\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on TM.todaymenuIdx = OL.todaymenuIdx\n" +
                "LEFT JOIN Menu M on M.menuIdx = TM.menuIdx\n" +
                "LEFT JOIN Stores S on O.storeIdx = S.storeIdx\n" +
                "WHERE O.customerIdx = ?  AND O.status = 'A'\n" +
                "GROUP BY O.orderIdx\n" +
                "ORDER BY O.created";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetOrderListRes(
                        rs.getInt("orderIdx"),
                        rs.getString("orderDate"),
                        rs.getInt("weekDay"),
                        weekDays[rs.getInt("weekDay")],
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("menu_name"),
                        rs.getString("menu_url"),
                        rs.getInt("price"),
                        rs.getInt("orerItemCount")
                ), customerIdx);
    }

    public OrderDetailRes getOrderDetail(int orderIdx) {
        String query = "SELECT\n" +
                "    DATE_FORMAT(O.created, '%Y년 %m월 %d일') as orderDate,\n" +
                "    DATE_FORMAT(O.created, '%h:%i:%s') as orderTime,\n" +
                "    DATE_FORMAT(O.created, '%p') as pmam,\n" +
                "    S.storeIdx, S.store_name, S.store_phone,\n" +
                "    CONCAT( M.menu_name, ' ', TM.price, '원') simpleMenu,\n" +
                "    COUNT(OL.orderIdx) count,\n" +
                "    SUM(OL.cnt * TM.price) as totalPrice,\n" +
                "    O.payment_status,\n" +
                "    O.request\n" +
                "FROM Orders O\n" +
                "    LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "    LEFT JOIN TodayMenu TM on TM.todaymenuIdx = OL.todaymenuIdx\n" +
                "    LEFT JOIN Menu M on M.menuIdx = TM.menuIdx\n" +
                "    LEFT JOIN Stores S on O.storeIdx = S.storeIdx\n" +
                "WHERE O.orderIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new OrderDetailRes(
                        rs.getString("orderDate") + " "+
                            rs.getString("orderTime") +
                            (rs.getString("pmam").equals("AM") ? "AM" : "PM"),
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("store_phone"),
                        rs.getString("simpleMenu")
                                + (rs.getInt("count") >= 2 ?
                                " 외" + (rs.getInt("count") -1) + "개": ""),
                        null,
                        rs.getInt("count"),
                        rs.getInt("totalPrice"),
                        rs.getString("payment_status"),
                        rs.getString("request")
                ), orderIdx);
    }

    public List<OrderMenuItem> getOrderItems(int orderIdx) {
        String query = "SELECT\n" +
                "    M.menu_name, \n" +
                "    OL.cnt,\n" +
                "    M.price, TM.discount, TM.price as todayPrice\n" +
                "FROM Orders O \n" +
                "    LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "    LEFT JOIN TodayMenu TM on TM.todaymenuIdx = OL.todaymenuIdx\n" +
                "    LEFT JOIN Menu M on M.menuIdx = TM.menuIdx\n" +
                "WHERE O.orderIdx = ?";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new OrderMenuItem(
                        rs.getString("menu_name"),
                        rs.getInt("cnt"),
                        rs.getInt("price"),
                        rs.getInt("discount"),
                        rs.getInt("todayPrice")
                ), orderIdx);
    }

    public int orderDelete(OrderDeleteReq delReq) {
        String query = "UPDATE Orders\n" +
                "    SET status = 'AD'\n" +
                "WHERE orderIdx = ?";

        return this.jdbcTemplate.update(query, delReq.getOrderIdx());

    }

    public int orderListDelete(OrderDeleteReq delReq) {
        String query = "UPDATE OrderLists\n" +
                "    SET status = 'AD'\n" +
                "WHERE orderIdx = ?";

        return this.jdbcTemplate.update(query, delReq.getOrderIdx());

    }
}
