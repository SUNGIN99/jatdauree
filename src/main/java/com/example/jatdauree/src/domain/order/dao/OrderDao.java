package com.example.jatdauree.src.domain.order.dao;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.order.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class OrderDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {this.jdbcTemplate = new JdbcTemplate(dataSource);}

    /**
     * OrdersDao-1
     * 23.07.06 작성자 : 윤다은
     * storeIdx로 상태가 'W'인 Orders 조회하기
     */
    public List<GetOrderItemRes> getOrdersByStoreIdx(int storeIdx){
        String OrdersQuery ="SELECT\n" +
                "    *\n" +
                "FROM(\n" +
                "    SELECT O.orderIdx, O.order_time, O.pickup_time,\n" +
                "           O.request, SUM(OL.cnt * TM.price) AS total_price,\n" +
                "           O.payment_status,\n" +
                "           M.menu_name,\n" +
                "           M.price as menu_price,\n" +
                "           TM.discount,\n" +
                "           TM.price as today_menu_price,\n" +
                "           OL.cnt,\n" +
                "           O.status,\n" +
                "           C.customerIdx\n" +
                "    FROM OrderLists OL\n" +
                "    LEFT JOIN Orders O on OL.orderIdx = O.orderIdx\n" +
                "    LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "    LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "    LEFT JOIN Customers C on O.customerIdx = C.customerIdx\n" +
                "    WHERE O.storeIdx = ? AND O.status = 'W'\n" +
                "    GROUP BY O.orderIdx, OL.orderlistIdx) R\n" +
                "    ORDER BY R.order_time;";
        return this.jdbcTemplate.query(OrdersQuery,
                (rs, rowNum) -> new GetOrderItemRes(
                        rs.getInt("orderIdx"),
                        rs.getString("order_time"),
                        rs.getString("pickup_time"),
                        rs.getString("request"),
                        rs.getInt("total_price"),
                        rs.getString("payment_status"),
                        rs.getString("menu_name"),
                        rs.getInt("cnt"),
                        rs.getInt("today_menu_price")
                ), storeIdx);
    }

    /**
     * OrdersDao-2
     * 23.07.06 작성자 : 윤다은
     * 주문 대기 페이지에서 "P","D"로 update해주기
     */
    public String checkOrderStatus(int orderIdx) {
        String query = "SELECT status FROM Orders WHERE orderIdx = ?";
        return this.jdbcTemplate.queryForObject(query, String.class, orderIdx);
    }// 현재 orderIdx의 status 값을 확인해준다.


    public int updateOrderStatus(int storeIdx,int orderIdx, String status){
        String updateQuery = "UPDATE Orders SET status = ? WHERE storeIdx = ? AND orderIdx = ?";
        return jdbcTemplate.update(updateQuery, status, storeIdx, orderIdx);
    }


    /**
     * OrdersDao-2
     * 23.07.07 작성자 : 윤다은
     * 주문 처리 페이지 Select (주문 번호 필요함)
     */
    public GetBillRes getOrderBills(int storeIdx, int orderIdx) {
        String query = "SELECT\n" +
                "    O.orderIdx, DATE_FORMAT(O.order_time, '%Y-%m-%d') AS orderDate,\n" +
                "    O.payment_status, DATE_FORMAT(O.pickup_time, '%H:%i') AS pickUpTime,\n" +
                "    O.request\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on OL.orderIdx = O.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE O.orderIdx = ?\n";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetBillRes(
                        storeIdx,
                        rs.getInt("orderIdx"),
                        rs.getString("orderDate"),
                        rs.getString("payment_status"),
                        rs.getString("pickUpTime"),
                        rs.getString("request"),
                        null
                ),orderIdx);

    }

    public List<OrderMenuCnt> getOrderMenus(int storeIdx, int orderIdx){
        String query = "SELECT M.menu_name, OL.cnt\n" +
                "FROM Orders O\n" +
                "LEFT JOIN  OrderLists OL on OL.orderIdx = O.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE O.orderIdx = ?\n" +
                "ORDER BY O.orderIdx;";   //join 할 때 큰 것에서 작은 것으로 join하기
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new OrderMenuCnt(
                        rs.getString("menu_name"),
                        rs.getInt("cnt")
                ), orderIdx);
    }

    /**
     * OrdersDao-3
     * 23.07.13 작성자 : 윤다은
     * 주문 처리 중 페이지 조회하기
     */
    public List<GetOrderItem> orderByStoreIdx(int storeIdx) {  //total_price를 한눈에 받아올 수 있도록 하기
        String query = "SELECT\n *" +
                "FROM(SELECT O.orderIdx,DATE_FORMAT(O.order_time,'%H:%i:%s') AS order_time, " +
                "DATE_FORMAT(O.pickup_time,'%H:%i:%s') AS pickup_time," +
                "O.request, COUNT(OL.orderlistIdx) AS orderCount, SUM(OL.cnt * TM.price) AS totalPrice,O.payment_status\n" +
                "FROM OrderLists OL\n" +
                "LEFT JOIN Orders O on OL.orderIdx = O.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE O.status = 'P'and O.storeIdx = ? \n" +
                "GROUP BY O.orderIdx) R\n" +
                "ORDER BY R.orderIdx;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetOrderItem(
                        rs.getInt("orderIdx"),
                        rs.getString("order_time"),
                        rs.getString("pickup_time"),
                        rs.getString("request"),
                        rs.getInt("orderCount"),
                        rs.getInt("totalPrice"),
                        rs.getString("payment_status"),
                        null
                ),storeIdx);

    }

    public List<OrderMenuCnt> orderItem(int storeIdx, int orderIdx) {
        String query = "SELECT M.menu_name, OL.cnt\n" +
                "FROM OrderLists OL\n" +
                "LEFT JOIN Orders O ON OL.orderIdx = O.orderIdx\n" +
                "LEFT JOIN TodayMenu TM ON OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M ON TM.menuIdx = M.menuIdx\n" +
                "WHERE O.status = 'P' " +
                "    AND O.storeIdx = ? " +
                "    AND O.orderIdx = ? \n" +
                "GROUP BY O.orderIdx, OL.orderlistIdx;" ;

        Object[] params = new Object[]{
                storeIdx,
                orderIdx
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new OrderMenuCnt(
                        rs.getString("menu_name"),
                        rs.getInt("cnt")
                ),params);
    }


    public int orderPickupUpdate(int orderIdx) throws BaseException {

        String updatequery = "UPDATE Orders " +
                "    SET status = 'A' \n " +
                "WHERE " +
                "    orderIdx = ? AND" +
                "    status = 'P'";
        return jdbcTemplate.update(updatequery, orderIdx);
    }

}