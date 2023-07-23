package com.example.jatdauree.src.domain.web.sales.dao;

import com.example.jatdauree.src.domain.web.sales.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SalesDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public TodayTotalSalesRes getTodayTotalSales(int storeIdx) {
        String query = "SELECT\n" +
                "    DATE_FORMAT(O.created, '%Y-%c-%d') as today,\n" +
                "    SUM(TM.price * OL.cnt) as total_price \n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "WHERE O.storeIdx = ?\n" +
                "  AND O.created >= CONCAT(DATE_FORMAT(NOW(), '%Y-%c-%d'), ' 00:00:00')\n" +
                "  AND O.created <= CONCAT(DATE_FORMAT(NOW(), '%Y-%c-%d'), ' 23:59:59')\n" +
                "  AND O.status = 'A'";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new TodayTotalSalesRes(
                        storeIdx,
                        rs.getString("today"),
                        rs.getInt("total_price")
                ), storeIdx);

    }


    public List<SalesByTime> getFromYtoTdaySales(int storeIdx, String dayStart, String dayFinish) {
        String query = "SELECT\n" +
                "    DATE_FORMAT(O.created, '%Y-%c-%d %H') as dayTime,\n" +
                "    SUM(TM.price * OL.cnt) as total_price_of_the_day\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "WHERE O.storeIdx = ? \n" +
                "  AND O.created >= ? \n" +
                "  AND O.created <= ? \n" +
                "AND O.status = 'A'\n" +
                "GROUP BY dayTime \n" +
                "ORDER BY dayTime";

        Object[] params = new Object[]{
                storeIdx, dayStart, dayFinish
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new SalesByTime(
                        rs.getString("dayTime"),
                        rs.getInt("total_price_of_the_day")
                ), params);
    }

    public List<SalesByWeekDay> getFromLtoTWeekSales(int storeIdx, String dayStart, String dayFinish){
        String query = "SELECT\n" +
                "    DATE_FORMAT(O.created, '%w') as weekDayId,\n" +
                "    DATE_FORMAT(O.created, '%Y-%c-%d') as weekDate,\n" +
                "    SUM(TM.price * OL.cnt) as total_price_of_the_weekDay\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "WHERE  O.storeIdx = ?\n" +
                "  AND O.created >= ?\n" +
                "  AND O.created <= ?\n" +
                "  AND O.status = 'A'\n" +
                "GROUP BY weekDayId\n" +
                "ORDER BY weekDayId";

        Object[] params = new Object[]{
                storeIdx, dayStart, dayFinish
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new SalesByWeekDay(
                        rs.getString("weekDate"),
                        rs.getInt("weekDayId"),
                        rs.getInt("total_price_of_the_weekDay")
                ), params);

    }

    public List<ItemSalesReOrNew> getMontlyMenuSales(int month, int storeIdx, int reOrder){
        String query = "SELECT\n" +
                "    DATE_FORMAT(O.created, '%Y-%c') as `month`,\n" +
                "    M.menuIdx, M.menu_name,\n" +
                "    COUNT(O.customerIdx) as reOrderCount,\n" +
                "    SUM(TM.price * OL.cnt) as monthly_sales\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE O.storeIdx = ?\n" +
                "  AND O.status = 'A'\n" +
                "  AND DATE_FORMAT(O.created, '%c') = ?\n" +
                "GROUP BY `month`, M.menuIdx\n" +
                "HAVING COUNT(O.customerIdx) " + (reOrder == 1 ? ">= 2" : "= 1");

        Object[] params = new Object[]{
                storeIdx, month
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ItemSalesReOrNew(
                        rs.getInt("menuIdx"),
                        rs.getString("menu_name"),
                        reOrder == 1 ? rs.getInt("reOrderCount") : 0, // 재주문 횟수
                        reOrder == 1 ? rs.getInt("monthly_sales") : 0, // 재주문 매출
                        reOrder == 0 ? rs.getInt("reOrderCount") : 0, // 신규주문 횟수
                        reOrder == 0 ? rs.getInt("monthly_sales") : 0, // 신규주문 매출
                        0
                ), params);

    }

    public int getStoresTotalOrderCount(int storeIdx) {
        String query = "SELECT\n" +
                "    COUNT(M.menuIdx) as total_menu_orders\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE O.storeIdx = ?\n" +
                "  AND O.status = 'A'";
        return this.jdbcTemplate.queryForObject(query, int.class, storeIdx);
    }

    public List<ItemSalesOrderRatio> getMontlyMenuOrders(int storeIdx, int month) {
        String query = "SELECT\n" +
                "    M.menuIdx,\n" +
                "    M.menu_name,\n" +
                "    COUNT(M.menuIdx) as menu_order_count\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE O.storeIdx = ?\n" +
                "  AND O.status = 'A'\n" +
                "  AND DATE_FORMAT(O.created, '%c') = ?\n" +
                "GROUP BY M.menuIdx";

        Object[] params = new Object[] {
                storeIdx, month
        };

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ItemSalesOrderRatio(
                        rs.getInt("menuIdx"),
                        rs.getString("menu_name"),
                        rs.getInt("menu_order_count"),
                        0
                ), params);

    }
}
