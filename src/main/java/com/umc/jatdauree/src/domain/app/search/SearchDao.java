package com.umc.jatdauree.src.domain.app.search;

import com.umc.jatdauree.src.domain.app.basket.dto.GetStoreList;
import com.umc.jatdauree.src.domain.app.search.dto.PostSearchReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SearchDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetStoreList> postSearch(int userIdx, PostSearchReq searchReq) {
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
                "LEFT JOIN StroeCategories SC on S.categoryIdx = SC.categoryIdx\n" +
                "WHERE (store_name LIKE ? OR category_name LIKE ?)\n" +
                "GROUP BY (S.storeIdx)";

        Object[] params = new Object[]{
                userIdx,
                "%"+searchReq.getSearchWord()+"%",
                "%"+searchReq.getSearchWord()+"%"
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

    public void serachRecord(int userIdx, PostSearchReq searchReq) {
        String query = "INSERT INTO Search(customerIdx, search_word)\n" +
                "VALUES(?, ?)";

        Object[] params = new Object[]{
                userIdx,
                searchReq.getSearchWord()
        };

        this.jdbcTemplate.update(query, params);
    }

    public List<String> recentSearch(int userIdx) {
        String query = "SELECT\n" +
                "    DISTINCT(search_word)\n" +
                "FROM Search\n" +
                "WHERE customerIdx = ? AND status != 'D'\n" +
                "ORDER BY searchIdx DESC";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> rs.getString("search_word"), userIdx);
    }

    public List<String> popularSearch(String date) {
        String query = "SELECT\n" +
                "    search_word,\n" +
                "    searchCount\n" +
                "FROM (SELECT\n" +
                "        search_word,\n" +
                "        COUNT(search_word) as searchCount\n" +
                "        FROM Search\n" +
                "        WHERE DATEDIFF(?, created) <= 2\n" +
                "        GROUP BY search_word\n" +
                "        LIMIT 10) R\n" +
                "ORDER BY R.searchCount DESC";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> rs.getString("search_word"), date);

    }
}
