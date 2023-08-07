package com.example.jatdauree.src.domain.app.order.dao;

import com.example.jatdauree.src.domain.app.order.dto.GetOrderListRes;
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
                "    TM.price,\n" +
                "    COUNT(OL.orderIdx) as orerItemCount\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on TM.todaymenuIdx = OL.todaymenuIdx\n" +
                "LEFT JOIN Menu M on M.menuIdx = TM.menuIdx\n" +
                "LEFT JOIN Stores S on O.storeIdx = S.storeIdx\n" +
                "WHERE O.customerIdx = ? AND O.status = 'A'\n" +
                "GROUP BY O.orderIdx";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetOrderListRes(
                        rs.getInt("orderIdx"),
                        rs.getString("orderDate"),
                        rs.getInt("weekDay"),
                        weekDays[rs.getInt("weekDay")],
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("menu_name"),
                        rs.getInt("price"),
                        rs.getInt("orerItemCount")
                ), customerIdx);
    }
}
