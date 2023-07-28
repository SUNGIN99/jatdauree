package com.example.jatdauree.src.domain.app.store.dao;

import com.example.jatdauree.src.domain.app.store.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AppStoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * storeDao - 1 menu
     * 23.07.20 작성자 : 이윤채
     * 가게상세-메뉴 메인,사이드 클래스 나눠서하기
     */
    public List<GetAppStoreDetailMenuItem> getAppStoreDetailMenuList(int storeIdx, String status){ //getAppStoreDetailMenuList

        String query ="SELECT m.storeIdx, m.menuIdx, tm.todaymenuIdx, m.menu_name, tm.remain, m.menu_url," +
                "m.composition, m.price, tm.discount, tm.price AS today_price "  +
                "FROM Menu m "  +
                "JOIN TodayMenu tm ON m.menuIdx = tm.menuIdx "  +
                "WHERE m.storeIdx = ? AND m.status = ?";
        Object[] params = new Object[]{storeIdx,status};
        return this.jdbcTemplate.query(query,(rs, rowNum) -> new GetAppStoreDetailMenuItem(
                rs.getInt("storeIdx"),
                rs.getInt("menuIdx"),
                rs.getInt("todaymenuIdx"),
                rs.getString("menu_name"),
                rs.getString("menu_url"),
                rs.getString("composition"),
                rs.getInt("price"),
                rs.getInt("remain"),
                rs.getInt("discount"),
                rs.getInt("today_price")
        ),params);
    }

    /**
     * storeDao - 2 info
     * 23.07.20 작성자 : 이윤채
     * 가게상세-정보 이거 4개로 나눠서 하기 storeInfo(가게)/StatisticsInfo(통계)/sellerInfo(사업자)/ingredientInfo(원산지) dao나눠서,클래스로 나눠서 하기
     */

    //가게정보
    public GetAppStoreDetailStoreInfo getAppStoreDetailStoreInfo(int storeIdx){
        String query = "SELECT store_name, \n" +
                "       TIME_FORMAT(store_open, '%H:%i') AS store_open, \n" + //(수정) 운영시간 초 자르기
                "       TIME_FORMAT(store_close, '%H:%i') AS store_close, \n" +
                "       breakday, \n" +
                "       store_phone \n" +
                "FROM Stores \n" +
                "WHERE storeIdx = ?;";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetAppStoreDetailStoreInfo(
                        rs.getString("store_name"),
                        rs.getString("store_open"),
                        rs.getString("store_close"),
                        rs.getString("breakday"),
                        rs.getString("store_phone")
                ), storeIdx);
    }
    //가게통계

    public Integer orderCount(int storeIdx){
        String orderCountQuery = "SELECT COUNT(storeIdx) AS Order_Count FROM Orders WHERE storeIdx = ? AND status = 'A';"; // dao로 3개로 나눠서 가지고 오고 리뷰 카운트 D아닌거 조건 포함
        return  this.jdbcTemplate.queryForObject(orderCountQuery, Integer.class, storeIdx);
    }

    public Integer reviewCount(int storeIdx){
        String reviewCountQuery ="SELECT COUNT(storeIdx) AS Review_Count FROM Review WHERE storeIdx = ? AND status <> 'D';"; //리뷰에 상태가 D(삭제 된 리뷰)가 아닌것 만 COUNT 잠시만 R이 삭제 아닌가? 그럼 A만 인것만 couont하면 되는거 아닌가?
        return  this.jdbcTemplate.queryForObject(reviewCountQuery, Integer.class, storeIdx);
    }

    public Integer subscribeCount(int storeIdx) {
        String subscribeCountQuery = "SELECT COUNT(storeIdx) AS Subscribe_Count FROM Subscribe WHERE storeIdx = ? AND status = 'A';"; //생각해 보니까 구독을 한번도 안하면 테이블에 값이 없고 그 뒤로 부터는 상태값 A,D로 여부를 확인하니까 A인것만 count
        return this.jdbcTemplate.queryForObject(subscribeCountQuery, Integer.class, storeIdx);
    }


    //사업자정보
    public GetAppStoreDetailSellerInfo getStoreAppDetailSellerInfo(int storeIdx){ //getStoreAppDetailSellerInfo
        String query = "SELECT m.name AS seller_name, s.store_name, s.store_address " +
                "FROM Merchandisers m " +
                "JOIN Stores s ON m.sellerIdx = s.sellerIdx " +
                "WHERE s.storeIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetAppStoreDetailSellerInfo(
                        rs.getString("seller_name"),
                        rs.getString("store_name"),
                        rs.getString("store_address")
                ),storeIdx);
    }

    //원산지 표기
    public List<GetAppStoreDetailIngredientInfo> getStoreAppDetailIngredientInfo(int storeIdx){ //자바 문법으로 하나로 문자열  합치기 //getStoreAppDetailIngredientInfo
        String query = "SELECT CONCAT(ingredient_name, '(', origin, ')') AS ingredient_info FROM Ingredients WHERE storeIdx = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetAppStoreDetailIngredientInfo(
                        rs.getString("ingredient_info")
                ),storeIdx);
    }

    /**
     * storeDao - 3 review
     * 23.07.20 작성자 : 이윤채
     * 가게상세-리뷰 여기도  4개로 나눠서 reviewStarTotal/reviewCount/reviewItems dao나눠서,클래스로 나눠서 하기
     */
    public GetAppReviewStarRes reviewStarTotal(int storeIdx){
        String query ="SELECT\n" +
                "      ROUND(AVG(star),1) AS star_average,\n" +
                "      COUNT(CASE WHEN status <> 'D' THEN reviewIdx END) AS reviews_total,\n" + //(수정)D가 아닌 상태만 카운트 하기
                "      COUNT(comment) AS comment_total,\n" +
                "      COUNT(CASE WHEN star = 1 THEN 1 END) AS star1_count,\n" +
                "      COUNT(CASE WHEN star = 2 THEN 1 END) AS star2_count,\n" +
                "      COUNT(CASE WHEN star = 3 THEN 1 END) AS star3_count,\n" +
                "      COUNT(CASE WHEN star = 4 THEN 1 END) AS star4_count,\n" +
                "      COUNT(CASE WHEN star = 5 THEN 1 END) AS star5_count\n" +
                "      FROM Review\n" +
                "WHERE storeIdx = ?"; //D가 아닌거 넣기

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetAppReviewStarRes(
                        storeIdx,
                        rs.getDouble("star_average"),
                        rs.getInt("reviews_total"),
                        rs.getInt("comment_total"),
                        rs.getInt("star1_count"),
                        rs.getInt("star2_count"),
                        rs.getInt("star3_count"),
                        rs.getInt("star4_count"),
                        rs.getInt("star5_count")
                )
                ,storeIdx);
    }




    public List<String> appOrderTodayMenus(int storeIdx, int orderIdx) { //근데 List String으로 하면 todaymenuIdx는 필요없나?
        String query = "SELECT\n" +
                "    M.menu_name\n" +
                "FROM\n" +
                "    Orders O\n" +
                "JOIN\n" +
                "    OrderLists OL ON O.orderIdx = OL.orderIdx\n" +
                "JOIN\n" +
                "    TodayMenu TM ON OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "JOIN\n" +
                "    Menu M ON TM.menuIdx = M.menuIdx\n" +
                "WHERE\n" +
                "    O.storeIdx = ? AND O.orderIdx = ? AND O.status = 'A'\n" +
                "GROUP BY\n" +
                "    M.menu_name;";

        Object[] params = new Object[]{storeIdx, orderIdx};
        return this.jdbcTemplate.queryForList(query, String.class, params);
    }




    //리뷰 정보 가져오기
    public List<AppReviewItems> reviewItems(int storeIdx) { //코드 안바꿨으면 import해서 가져오기 ---> AppReviewItems 여기 안에있는 값이 바꿨기 때문에 못 가져옴
        String query = "SELECT\n" +
                "    O.orderIdx,\n" +
                "    R.reviewIdx,\n" +
                "    C.name,\n" +
                "    R.star ,\n" +
                "    R.contents, R.comment, R.review_url\n" +
                "FROM Orders O\n" +
                "JOIN Review R on O.orderIdx = R.orderIdx\n" +
                "JOIN Customers C ON R.customerIdx = C.customerIdx\n" +
                "WHERE\n" +
                "    R.storeIdx = ?\n" +
                "  AND O.status = 'A'\n" +
                "  AND R.status <> 'D'\n" +
                "ORDER BY reviewIdx;";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new AppReviewItems(
                        rs.getInt("orderIdx"),
                        rs.getInt("reviewIdx"),
                        rs.getString("name"),
                        rs.getInt("star"),
                        rs.getString("contents"),
                        rs.getString("comment") == null ? "" : rs.getString("comment"),
                        rs.getString("review_url"),
                        null

                ), storeIdx);
    }
    //가게 목록보기
        public List<GetAppStoreRes> getAppStoreList(int storeIdx) {
            String query = "SELECT s.storeIdx, s.store_logo_url, s.sign_url, s.categoryIdx, s.store_name, s.x AS longitude, s.y AS latitude, ROUND(AVG(r.star), 1) AS star_average, sub.status " +
                    "FROM Stores s " +
                    "LEFT JOIN Review r ON s.storeIdx = r.storeIdx " +
                    "LEFT JOIN Subscribe sub ON s.storeIdx = sub.storeIdx " +
                    "WHERE s.storeIdx =?";


            return this.jdbcTemplate.query(query,
                    (rs, rowNum) -> new GetAppStoreRes(
                            rs.getInt("storeIdx"),
                            rs.getString("store_logo_url"),
                            rs.getString("sign_url"),
                            rs.getInt("categoryIdx"),
                            rs.getString("store_name"),
                            rs.getDouble("longitude"),
                            rs.getDouble("latitude"),
                            rs.getInt("star_average"),
                            rs.getString("status")

                    ),storeIdx);

        }

    }



















