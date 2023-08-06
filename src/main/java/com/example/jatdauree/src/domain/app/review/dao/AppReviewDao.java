package com.example.jatdauree.src.domain.app.review.dao;

import com.example.jatdauree.src.domain.app.review.dto.MyReviews;
import com.example.jatdauree.src.domain.app.review.dto.PostReviewReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AppReviewDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {this.jdbcTemplate = new JdbcTemplate(dataSource);}



    /**
     * ReviewDao-1
     * 23.07.20 작성자 : 윤다은
     * 리뷰 작성하기
     */
    public void reviewPost(int customerIdx, PostReviewReq postReviewReq, String fileName) {
        String query = "INSERT INTO Review " +
                "(storeIdx, orderIdx, customerIdx, star, contents, review_url) " +
                "VALUES (?,?,?,?,?,?)";
        Object[] params = new Object[]{
                postReviewReq.getStoreIdx(),
                postReviewReq.getOrderIdx(),
                customerIdx,
                postReviewReq.getStars(),
                postReviewReq.getContents(),
                fileName != null ? fileName : null //fileName이 없는 경우 null로 들어감.

        };
        this.jdbcTemplate.update(query, params);
    }


    /**
     * ReviewDao-2
     * 23.07.20 작성자 : 윤다은
     * 마이 떨이에서 나의 모든 리뷰 조회
     */
    // 총 리뷰 개수
    public int countReviews(int customerIdx) {
        String query = "SELECT COUNT(reviewIdx) AS total_reviews\n" +
                "FROM Review\n" +
                "WHERE customerIdx = ?;";
        return jdbcTemplate.queryForObject(query, Integer.class, customerIdx);
    }

    // 나의 리뷰 조회 하기 => 몇달 전 이것도 표현해야 함
    public List<MyReviews> getMyReviews(int customerIdx) {
        String query = "SELECT " +
                "R.reviewIdx," +
                "C.name, C.customerIdx," +
                "R.star,R.created,R.review_url,R.contents,R.comment \n" +
                "FROM Review R \n" +
                "JOIN Customers C on C.customerIdx = R.customerIdx\n" +
                "WHERE R.customerIdx = ? ;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new MyReviews(
                        rs.getInt("reviewIdx"),
                        rs.getString("name"),
                        rs.getInt("star"),
                        rs.getString("created"),
                        rs.getString("review_url"),
                        rs.getString("contents"),
                        rs.getString("comment"),
                        null
                        //List.of(rs.getString("menu_name"))

                ),customerIdx);
    }

    // 메뉴 리스트 조회하기
    public List<String> myReviewsMenu(int customerIdx, int reviewIdx) {
        String query = "SELECT M.menu_name\n" +
                "FROM Review R\n" +
                "JOIN OrderLists OL on R.orderIdx = OL.orderIdx\n" +
                "JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "JOIN Menu M on M.menuIdx = TM.menuIdx\n" +
                "WHERE R.customerIdx = ? AND R.reviewIdx = ?;";
        return this.jdbcTemplate.queryForList(query, String.class, customerIdx,reviewIdx);

    }


    /**
     * ReviewDao-3
     * 23.07.22 작성자 : 윤다은
     * 리뷰 삭제하기
     */
    public String checkReviewStatus(int reviewIdx) {
        String query = "SELECT status FROM Review WHERE reviewIdx = ?";
        return this.jdbcTemplate.queryForObject(query, String.class,reviewIdx);
    } // 리뷰 상태 체크하기

    public int reviewDelete(int reviewIdx, int customerIdx) {
        String query = "UPDATE Review " +
                "SET status = 'D'\n" +
                "WHERE reviewIdx =? AND customerIdx = ?";
        return this.jdbcTemplate.update(query,reviewIdx,customerIdx);
    }// 리뷰 삭제하기


    /**
     * ReviewDao-4
     * 23.07.22 작성자 : 윤다은
     * 리뷰 신고하기
     */

    public int reportReview(int reviewIdx) {
        String reportquery = "UPDATE Review " +
                "SET status = 'R'\n" +
                "WHERE reviewIdx = ?";
        return this.jdbcTemplate.update(reportquery,reviewIdx);
    }
}
