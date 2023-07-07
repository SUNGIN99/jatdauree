package com.example.jatdauree.src.domain.order.dao;

import com.example.jatdauree.src.domain.order.dto.GetOrderProRes;
import com.example.jatdauree.src.domain.order.dto.GetOrderRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class OrderDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {this.jdbcTemplate = new JdbcTemplate(dataSource);}

    /**
     * OrdersDao-1
     * 23.07.04 작성자 : 윤다은
     * sellerIdx로 StoreIdx 조회하기
     */
    public int getStoreIdxBySellerIdx(int sellerIdx){
        String storeIdxQuery = "SELECT storeIdx FROM Stores WHERE sellerIdx = ? ";
        return this.jdbcTemplate.queryForObject(storeIdxQuery, int.class, sellerIdx);
    }


    /**
     * OrdersDao-2
     * 23.07.06 작성자 : 윤다은
     * storeIdx로 상태가 'W'인 Orders 조회하기
     */
    public List<GetOrderRes> getOrdersByStoreIdx(int storeIdx){
        String OrdersQuery ="SELECT O.orderIdx, S.store_name,C.uid, DATE_FORMAT(O.order_time, '%H:%i') AS order_time,COUNT(OL.orderIdx) AS total_menu, SUM(OL.cnt * M.price) AS total_price,\n" +
                "      M.menu_name, OL.cnt,\n" +
                "        DATE_FORMAT(O.pickup_time, '%H:%i') AS pickup_time,O.request,O.payment_status,O.status\n" +
                "FROM OrderLists OL\n" +
                "LEFT JOIN Orders O on OL.orderIdx = O.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "LEFT JOIN Stores S on O.storeIdx = S.storeIdx\n" +
                "LEFT JOIN Customers C on O.customerIdx = C.customerIdx\n" +
                "WHERE O.storeIdx = ? and O.status = 'W'"+
                "GROUP BY OL.orderlistIdx\n"+
                "ORDER BY O.order_time;";
        return this.jdbcTemplate.query(OrdersQuery,
                (rs, rowNum) -> new GetOrderRes(
                        rs.getInt("orderIdx"),
                        rs.getString("store_name"),
                        rs.getString("uid"),
                        rs.getString("order_time"),
                        rs.getInt("total_menu"),
                        rs.getInt("total_price"),
                        rs.getString("menu_name"),
                        rs.getString("cnt"),
                        rs.getString("pickup_time"),
                        rs.getString("request"),
                        rs.getString("payment_status"),
                        rs.getString("status")

                ) {
                }, storeIdx);
    }
    /**
     * OrdersDao-3
     * 23.07.06 작성자 : 윤다은
     * 주문 대기 페이지에서 "P","D"로 update해주기
     */
    public void updateOrderStatus(int storeIdx,int orderIdx, String status){
        String updateQuery = "UPDATE Orders SET status = ? WHERE storeIdx = ? AND orderIdx = ? AND status = 'W'";
        this.jdbcTemplate.update(updateQuery,status, storeIdx, orderIdx);
    }
    /**
     * OrdersDao-4
     * 23.07.07 작성자 : 윤다은
     * 주문 처리 페이지 Select (주문 번호 필요함)
     */
    public List<GetOrderProRes> getOrderProByStoreIdx(int storeIdx){
        String OrdersQuery ="SELECT O.orderIdx, S.store_name,C.uid, DATE_FORMAT(O.order_time, '%H:%i') AS order_time,COUNT(OL.orderIdx) AS total_menu, SUM(OL.cnt * M.price) AS total_price,\n" +
                "      M.menu_name, OL.cnt,\n" +
                "        DATE_FORMAT(O.pickup_time, '%H:%i') AS pickup_time,O.request,O.payment_status,O.status\n" +
                "FROM OrderLists OL\n" +
                "LEFT JOIN Orders O on OL.orderIdx = O.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "LEFT JOIN Stores S on O.storeIdx = S.storeIdx\n" +
                "LEFT JOIN Customers C on O.customerIdx = C.customerIdx\n" +
                "WHERE O.storeIdx = ? and O.status = 'P'"+
                "GROUP BY OL.orderlistIdx\n"+
                "ORDER BY O.order_time;";
        return this.jdbcTemplate.query(OrdersQuery,
                (rs, rowNum) -> new GetOrderProRes(
                        // rs.getInt("OrderNumber"),
                        rs.getInt("orderIdx"),
                        rs.getString("store_name"),
                        rs.getString("uid"),
                        rs.getString("order_time"),
                        rs.getInt("total_menu"),
                        rs.getInt("total_price"),
                        rs.getString("menu_name"),
                        rs.getString("cnt"),
                        rs.getString("pickup_time"),
                        rs.getString("request"),
                        rs.getString("payment_status"),
                        rs.getString("status")

                ) {
                }, storeIdx);
    }

    /**
     * OrdersDao-5
     * 23.07.07 작성자 : 윤다은
     * 픽업 완료 status를 A로 UPDATE
     */
    public void updateOrderPickup(int storeIdx,int orderIdx, String status){
        {
            String updateQuery = "UPDATE Orders SET status = ? WHERE storeIdx = ? AND orderIdx = ? AND status = 'P'";
            this.jdbcTemplate.update(updateQuery,status, storeIdx, orderIdx);
        }
    }
}
