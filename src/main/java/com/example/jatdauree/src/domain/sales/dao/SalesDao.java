package com.example.jatdauree.src.domain.sales.dao;

import com.example.jatdauree.src.domain.sales.dto.TodayTotalSalesRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
                "    SUM(TM.price) as total_price\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "WHERE O.storeIdx = ?\n" +
                "  AND DATE_FORMAT(O.created, '%Y-%c-%d') = DATE_FORMAT(NOW(), '%Y-%c-%d')\n" +
                "  AND status = 'A'";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new TodayTotalSalesRes(
                        storeIdx,
                        rs.getString("today"),
                        rs.getInt("total_price")
                ), storeIdx);

    }
}
