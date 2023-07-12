package com.example.jatdauree.src.domain.sales.dao;

import com.example.jatdauree.src.domain.sales.dto.SalesByTime;
import com.example.jatdauree.src.domain.sales.dto.SalesByWeekDay;
import com.example.jatdauree.src.domain.sales.dto.TodayTotalSalesRes;
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
                "    SUM(TM.price * OL.cnt)\n" +
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
}
