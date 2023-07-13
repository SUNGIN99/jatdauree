package com.example.jatdauree.src.domain.review.dao;

import com.example.jatdauree.src.domain.review.dto.*;
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
    public List<OrderTodayMenu> orderTodayMenus(int storeIdx, int orderIdx) {
        String query = "SELECT TM.todaymenuIdx, M.menu_name, OL.cnt\n" +
                "FROM Orders O\n" +
                "   JOIN OrderLists OL ON O.orderIdx = OL.orderIdx\n" +
                "   JOIN TodayMenu TM ON OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "   JOIN Menu M ON TM.menuIdx = M.menuIdx\n" +
                "WHERE\n" +
                "   O.storeIdx =? AND O.orderIdx =? AND O.status = 'A' ";  // 23.07.13 상태값 고객이쓴 리뷰 정보 없으므로 수정 필요

        Object[] params = new Object[]{storeIdx, orderIdx};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new OrderTodayMenu(
                        rs.getInt("todayMenuIdx"),
                        rs.getString("menu_name"),
                        rs.getInt("cnt")
                ), params);
    }


    //리뷰 정보 가져오기
    public List<ReviewItems> reviewItems(int storeIdx) {
        String query = "SELECT O.orderIdx, R.reviewIds, C.name,R.star,R.contents, R.review_url\n" +
                " FROM Orders O\n" +
                "    JOIN Review R on O.orderIdx = R.orderIdx\n" +
                "    JOIN Customers C ON R.customerIdx = C.customerIdx\n" +
                " WHERE\n" +
                "    R.storeIdx = ? AND O.status = 'A';"; // 23.07.13 상태값 고객이쓴 리뷰 정보 없으므로 수정 필요

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ReviewItems(
                        rs.getInt("orderIdx"),
                        rs.getInt("reviewIdx"),
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

    //해당 리뷰인덱스가 존재하는지 확인
    public int checkReviewIdx(int storeIdx,int reviewIdx) {
        String query = "SELECT EXISTS (SELECT * FROM Review WHERE storeIdx=? AND reviewIdx= ? limit 1)";

        Object[] params = new Object[]{storeIdx,reviewIdx};
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }


    //해당 리뷰인덱스에 comment 넣기(등록,수정)
    public void reviewAnswer(ReviewAnswerReq reviewAnswerReq){
        String query = "UPDATE Review SET comment = ? WHERE reviewIdx= ?;";

        Object[] params = new Object[]{reviewAnswerReq.getComment(),reviewAnswerReq.getReviewIdx()};

        this.jdbcTemplate.update(query,params);
    }


}


