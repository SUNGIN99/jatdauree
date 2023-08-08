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
    public int reviewPost(int customerIdx, PostReviewReq postReviewReq, String fileName) {
        String query = "INSERT INTO Review " +
                "(storeIdx, orderIdx, customerIdx, star, contents, review_url) " +
                "VALUES (?,?,?,?,?,?)";
        Object[] params = new Object[]{
                postReviewReq.getStoreIdx(),
                postReviewReq.getOrderIdx(),
                customerIdx,
                postReviewReq.getStars(),
                postReviewReq.getContents(),
                fileName//fileName이 없는 경우 null로 들어감.

        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
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
                "WHERE customerIdx = ? AND status != 'D';";
        return jdbcTemplate.queryForObject(query, Integer.class, customerIdx);
    }

    // 나의 리뷰 조회 하기 => 몇달 전 이것도 표현해야 함
    public List<MyReviews> getMyReviews(int customerIdx) {
        String query = "SELECT\n" +
                "    R.storeIdx, S.store_name,\n" +
                "    R.reviewIdx, R.star, R.created, R.review_url, R.contents, R.comment\n" +
                "FROM Review R\n" +
                "LEFT JOIN Customers C on R.customerIdx = C.customerIdx\n" +
                "LEFT JOIN Stores S on R.storeIdx = S.storeIdx\n" +
                "WHERE R.customerIdx = ? AND status != 'D'";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new MyReviews(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getInt("reviewIdx"),
                        rs.getInt("star"),
                        rs.getString("created"),
                        rs.getString("review_url"),
                        rs.getString("contents"),
                        rs.getString("comment"),
                        null
                ),customerIdx);
    }

    // 메뉴 리스트 조회하기
    public List<String> myReviewsMenu(int reviewIdx) {
        String query = "SELECT\n" +
                "    M.menu_name\n" +
                "FROM Review R\n" +
                "LEFT JOIN Orders O on R.orderIdx = O.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on O.storeIdx = TM.storeIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE R.reviewIdx = ?";

        return this.jdbcTemplate.queryForList(query, String.class, reviewIdx);

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

    public int reviewDelete(int reviewIdx) {
        String query = "UPDATE Review " +
                "SET status = 'D'\n" +
                "WHERE reviewIdx =?";

        return this.jdbcTemplate.update(query, reviewIdx);
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
