package com.example.jatdauree.src.domain.web.order.dao;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.web.order.dto.GetBillRes;
import com.example.jatdauree.src.domain.web.order.dto.GetOrderItemRes;
import com.example.jatdauree.src.domain.web.order.dto.OrderMenuCnt;
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
    public List<GetOrderItemRes> getOrdersByStoreIdx(int storeIdx, String status){
        String OrdersQuery ="SELECT\n" +
                "    *\n" +
                "FROM(\n" +
                "    SELECT O.orderIdx, O.order_sequence, O.order_time, O.pickup_time,\n" +
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
                "    WHERE O.storeIdx = ? AND O.status = ?\n" +
                "    GROUP BY O.orderIdx, OL.orderlistIdx) R\n" +
                "    ORDER BY R.order_time;";
        return this.jdbcTemplate.query(OrdersQuery,
                (rs, rowNum) -> new GetOrderItemRes(
                        rs.getInt("orderIdx"),
                        rs.getInt("order_sequence") == 0 ? 0 : rs.getInt("order_sequence"),
                        rs.getString("order_time"),
                        rs.getString("pickup_time"),
                        rs.getString("request") == null ? "": rs.getString("request"),
                        rs.getInt("total_price"),
                        rs.getString("payment_status"),
                        rs.getString("menu_name"),
                        rs.getInt("cnt"),
                        rs.getInt("today_menu_price")
                ), storeIdx, status);
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


    public int orderDenied(int storeIdx, int orderIdx, String status){
        String updateQuery = "UPDATE Orders SET " +
                "status = ? " +
                "WHERE storeIdx = ? AND orderIdx = ?";
        return jdbcTemplate.update(updateQuery, status, storeIdx, orderIdx);
    }

    public int orderAccepted(int storeIdx, int orderIdx, String status, int orderSequence){
        String updateQuery = "UPDATE Orders SET " +
                "status = ?, " +
                "order_sequence = ? " +
                "WHERE storeIdx = ? AND orderIdx = ?";
        return jdbcTemplate.update(updateQuery, status, orderSequence, storeIdx, orderIdx);
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
                "    O.order_sequence, \n" +
                "    O.request\n" +
                "FROM Orders O\n" +
                "WHERE O.orderIdx = ?\n";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetBillRes(
                        storeIdx,
                        rs.getInt("orderIdx"),
                        rs.getString("orderDate"),
                        rs.getString("payment_status"),
                        rs.getString("pickUpTime"),
                        rs.getInt("order_sequence"),//0, // 주문 번호
                        rs.getString("request"),
                        null
                ),orderIdx);

    }

    public List<OrderMenuCnt> getOrderMenus(int orderIdx){
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


    public int orderPickupUpdate(int orderIdx) throws BaseException {
        String updatequery = "UPDATE Orders " +
                "    SET status = 'A' \n " +
                "WHERE " +
                "    orderIdx = ? AND" +
                "    status = 'P'";
        return jdbcTemplate.update(updatequery, orderIdx);
    }

    public int getOrderSequence(int storeIdx, String date) {
        String query = "SELECT\n" +
                "    orderIdx,\n" +
                "    order_sequence\n" +
                "FROM Orders\n" +
                "WHERE storeIdx = ?\n" +
                "  AND DATE_FORMAT(order_time, '%Y-%m-%d') = ?\n" +
                "  AND (status = 'P' OR status = 'A')\n" +
                "ORDER BY order_sequence DESC LIMIT 1";

        Object[] params = new Object[]{storeIdx, date};

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("order_sequence"), params);
    }
}