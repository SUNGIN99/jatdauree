package com.example.jatdauree.src.domain.app.store.dao;

import com.example.jatdauree.src.domain.app.basket.dto.GetStoreList;
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

        String query ="SELECT m.storeIdx, m.menuIdx, tm.todaymenuIdx, m.menu_name, tm.remain, m.menu_url,\n" +
                "   m.composition, m.price, tm.discount, tm.price AS today_price \n" +
                "   FROM TodayMenu tm \n" +
                "   JOIN Menu m ON m.menuIdx = tm.menuIdx \n" +
                "   WHERE tm.storeIdx = ? AND m.status = ?";
        Object[] params = new Object[]{storeIdx,status};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetAppStoreDetailMenuItem(
                    rs.getInt("menuIdx"),
                    rs.getInt("todaymenuIdx"),
                    rs.getString("menu_name"),
                    rs.getString("menu_url"),
                    rs.getString("composition"),
                    rs.getInt("remain"),
                    rs.getInt("price"),
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

    public int orderCount(int storeIdx){
        String orderCountQuery = "SELECT " +
                "COUNT(storeIdx) AS Order_Count " +
                "FROM Orders " +
                "WHERE storeIdx = ? AND status = 'A';";

        return  this.jdbcTemplate.queryForObject(orderCountQuery, int.class, storeIdx);
    }

    public int reviewCount(int storeIdx){
        String reviewCountQuery ="SELECT " +
                "COUNT(storeIdx) AS Review_Count " +
                "FROM Review " +
                "WHERE storeIdx = ? AND status <> 'D';";

        return  this.jdbcTemplate.queryForObject(reviewCountQuery, int.class, storeIdx);
    }

    public int subscribeCount(int storeIdx) {
        String subscribeCountQuery = "SELECT " +
                "COUNT(storeIdx) AS Subscribe_Count " +
                "FROM Subscribe WHERE storeIdx = ? AND status = 'A';";

        return this.jdbcTemplate.queryForObject(subscribeCountQuery, int.class, storeIdx);
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
                        rs.getString("store_address"),
                        "123-456-789"
                ),storeIdx);
    }

    //원산지 표기
    public String getStoreAppDetailIngredientInfo(int storeIdx){ //자바 문법으로 하나로 문자열  합치기 //getStoreAppDetailIngredientInfo
        String query = "SELECT\n" +
                "    group_concat(ingredient_name, '(', origin, ') ') AS ingredient_info\n" +
                "FROM Ingredients\n" +
                "WHERE storeIdx = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getString("ingredient_info"),
                storeIdx);
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
                "WHERE storeIdx = ? AND status != 'D'"; //D가 아닌거 넣기

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




    public List<String> appOrderTodayMenus(int orderIdx) { //근데 List String으로 하면 todaymenuIdx는 필요없나?
        String query = "SELECT\n" +
                "    M.menu_name\n" +
                "FROM Orders O\n" +
                "LEFT JOIN OrderLists OL on O.orderIdx = OL.orderIdx\n" +
                "LEFT JOIN TodayMenu TM on OL.todaymenuIdx = TM.todaymenuIdx\n" +
                "LEFT JOIN Menu M on TM.menuIdx = M.menuIdx\n" +
                "WHERE O.orderIdx = ?\n" +
                "AND O.status = 'A'";

        return this.jdbcTemplate.queryForList(query, String.class, orderIdx);
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
                        null,
                        rs.getString("name"),
                        rs.getInt("star"),
                        rs.getString("contents"),
                        rs.getString("comment") == null ? "" : rs.getString("comment"),
                        rs.getString("review_url"),
                        null

                ), storeIdx);
    }

    public List<StoreListXY> getStoreListByAddr(double[] aroundXY) {
        String query = "SELECT\n" +
                "    storeIdx,\n" +
                "    S.categoryIdx,\n" +
                "    store_name,\n" +
                "    store_address,\n" +
                "    x,y\n" +
                "FROM Stores S\n" +
                "LEFT JOIN StroeCategories SC on S.categoryIdx = SC.categoryIdx\n" +
                "WHERE x BETWEEN ? AND ?\n" +
                "AND y BETWEEN ? AND ?";

        //aroundXY = {minX, maxX, minY, maxY}
        Object[] params = new Object[]{aroundXY[0],aroundXY[1],aroundXY[2],aroundXY[3]};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new StoreListXY(
                        rs.getInt("storeIdx"),
                        rs.getInt("categoryIdx"),
                        rs.getString("store_name"),
                        rs.getString("store_address"),
                        rs.getDouble("x"),
                        rs.getDouble("y")
                ), params);
        }

    public StorePreviewDetails getStorePreview(int storeIdx) {
        String query = "SELECT\n" +
                "    storeIdx,\n" +
                "    store_name,\n" +
                "    store_logo_url,\n" +
                "    sign_url,\n" +
                "    x, y\n" +
                "FROM Stores\n" +
                "WHERE storeIdx= ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new StorePreviewDetails(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("store_logo_url"),
                        rs.getString("sign_url"),
                        rs.getDouble("x"),
                        rs.getDouble("y")
                ), storeIdx);
    }

    public List<StorePreviewDetails> getAroundPreview(int storeIdx, double nowX, double nowY, double[] aroundXY) {
        String query =
                "SELECT\n" +
                "    storeIdx,\n" +
                "    store_name,\n" +
                "    store_logo_url,\n" +
                "    sign_url,\n" +
                "    x,y,\n" +
                "    @dLat = RADIANS(y - ?) as dLat,\n" + // 1
                "    @dLon = RADIANS(x - ?) as dLon,\n" + // 2
                "    @a = SIN(@dLat/2) * SIN(@dlat/2) + COS(RADIANS(?)) * COS(RADIANS(y)) * SIN(@dLon/2) * SIN(@dLon/2) as a,\n" + // 3
                "    @c = 2 * atan2(SQRT(@a), SQRT(1-@a)) as c,\n" +
                "    @d = 6731 * @c * 1000 as distance\n" +
                "FROM Stores S\n" +
                "WHERE storeIdx != ?\n" + // 0
                "AND x BETWEEN ? AND ?\n" + // 4, 5
                "AND y BETWEEN ? AND ?\n" + // 6, 7
                "ORDER BY @d ASC LIMIT 2";
        Object[] params = new Object[]{
                nowY, nowX, // 1, 2
                nowY, // 3
                storeIdx, // 0
                aroundXY[0],aroundXY[1],aroundXY[2],aroundXY[3]};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new StorePreviewDetails(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("store_logo_url"),
                        rs.getString("sign_url"),
                        rs.getDouble("x"),
                        rs.getDouble("y")
                ), params);
    }

    public double getStoreStar(int storeIdx) {
        String query = "SELECT\n" +
                "    ROUND(AVG(star), 1) AS star_average \n" +
                "FROM Review\n" +
                "WHERE storeIdx = ? AND status != 'D'";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getDouble("star_average")
                , storeIdx);
    }

    public int getStoreSubscribed(int customerIdx, int storeIdx) {
        String query = "SELECT EXISTS(SELECT\n" +
                "    *\n" +
                "FROM Subscribe\n" +
                "WHERE customerIdx = ? AND storeIdx = ?\n" +
                "AND status != 'D')";

        Object[] params = new Object[]{customerIdx, storeIdx};

        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    // --- 윤채
    public GetAppStoreInfo getAppStoreInfo(int storeIdx){
        String query = "SELECT store_name, store_phone, x, y, store_address FROM Stores WHERE storeIdx = ?";
        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetAppStoreInfo(
                    rs.getString("store_name"),
                    rs.getString("store_phone"),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getString("store_address")
        ),storeIdx);
    }

    public List<GetStoreList> getAppStoreList(int userIdx, double[] aroundXY) {
        String query = "SELECT\n" +
                "    S.storeIdx,\n" +
                "    store_name,\n" +
                "    store_logo_url,\n" +
                "    sign_url,\n" +
                "    store_address,\n" +
                "    x, y,\n" +
                "    ROUND(AVG(star), 1) as star,\n" +
                "    SUB.customerIdx\n" +
                "FROM Stores S\n" +
                "LEFT JOIN Review R on S.storeIdx = R.storeIdx AND R.status != 'D'\n" +
                "LEFT JOIN Subscribe SUB on S.storeIdx = SUB.storeIdx AND SUB.status != 'D' AND SUB.customerIdx = ?\n" +
                "WHERE x BETWEEN ? AND ?\n" +
                "AND y BETWEEN ? AND ?\n" +
                "GROUP BY (S.storeIdx)\n" +
                "ORDER BY ABS(?+?/2 - x) DESC , ABS(?+?/2 - y) DESC ;";

        Object[] params = new Object[]{
                userIdx,
                aroundXY[0], aroundXY[1],
                aroundXY[2], aroundXY[3],
                aroundXY[1], aroundXY[0],
                aroundXY[3], aroundXY[2],
        };


        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreList(
                        rs.getInt("storeIdx"),
                        rs.getString("store_name"),
                        rs.getString("store_logo_url"),
                        rs.getString("sign_url"),
                        rs.getString("store_address"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("star"),
                        rs.getInt("customerIdx")
                ), params);

    }

    public StorePoint storePoint(int storeIdx) {
        String query = "SELECT store_name, x, y FROM Stores WHERE storeIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) ->  new StorePoint(
                        rs.getString("store_name"),
                        rs.getDouble("x"),
                        rs.getDouble("y")
                ), storeIdx);
    }
}



















