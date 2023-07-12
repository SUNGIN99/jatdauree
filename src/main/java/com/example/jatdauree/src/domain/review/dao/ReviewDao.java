package com.example.jatdauree.src.domain.review.dao;

import com.example.jatdauree.src.domain.review.dto.OrderTodayMenu;
import com.example.jatdauree.src.domain.review.dto.ReviewItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    //주문 떨이 메뉴(이름,수량) 조회
    public List<OrderTodayMenu> orderTodayMenus(int storeIdx, int orderIdx){
        String query = "SELECT TM.todaymenuIdx, M.menu_name, OL.cnt\n"+
                "FROM Orders O\n"+
                "   JOIN OrderLists OL ON O.orderIdx = OL.orderIdx\n"+
                "   JOIN TodayMenu TM ON OL.todaymenuIdx = TM.todaymenuIdx\n"+
                "   JOIN Menu M ON TM.menuIdx = M.menuIdx\n"+
                "WHERE\n"+
                "   O.storeIdx =? AND O.orderIdx =?";

        Object[] params = new Object[]{storeIdx, orderIdx};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new OrderTodayMenu(
                        rs.getInt("todayMenuIdx"),
                        rs.getString("menu_name"),
                        rs.getInt("cnt")
                        ), params);
    }


    //리뷰 정보 가져오기
    public List<ReviewItems> reviewItems(int storeIdx){
        String query = "SELECT O.orderIdx, R.reviewIds, C.name,R.star,R.contents, R.review_url\n" +
                " FROM Orders O\n" +
                "    JOIN Review R on O.orderIdx = R.orderIdx\n" +
                "    JOIN Customers C ON R.customerIdx = C.customerIdx\n" +
                " WHERE\n" +
                "    R.storeIdx = ?;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ReviewItems(
                        rs.getInt("orderIdx"),
                        rs.getInt("reviewIds"),
                        rs.getString("name"),
                        rs.getInt("star"),
                        rs.getString("contents"),
                        rs.getString("review_url"),
                        null
                )

                /*
                    {int orderIdx = rs.getInt("orderIdx");
                    int reviewIdx = rs.getInt("reviewIds");
                    String customerName = rs.getString("name");
                    int star = rs.getInt("star");
                    String contents = rs.getString("contents");
                    String reviewUrl = rs.getString("review_url");

                    //List<OrderTodayMenu> orderTodayMenus = orderTodayMenus(storeIdx, orderIdx); //list 타입이라서 아예 list를 넣었음.

                    return new ReviewItems(orderIdx, reviewIdx, customerName, star, contents, reviewUrl, null);}*/
                , storeIdx);

    }

    public String getStatus(int storeIdx){

    }

}
