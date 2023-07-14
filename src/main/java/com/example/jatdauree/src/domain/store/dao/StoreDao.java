package com.example.jatdauree.src.domain.store.dao;

import com.example.jatdauree.src.domain.store.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * storeDao - 0 - 1
     * 23.07.06 작성자 : 김성인
     * 등록된 판매자 가게 조회
     */
    public String storeNameBySellerIdx(int sellerIdx){
        String query = "SELECT store_name FROM Stores WHERE sellerIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getString("store_name"), sellerIdx);
    }

    /**
     * storeDao - 0 - 2
     * 23.07.06 작성자 : 김성인
     * 등록된 판매자 가게 Idx 조회
     */
    public int storeIdxBySellerIdx(int sellerIdx){
        String query = "SELECT storeIdx FROM Stores WHERE sellerIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> rs.getInt("storeIdx"), sellerIdx);
    }


    /**
     * storeDao - 1
     * 23.07.06 작성자 : 이윤채
     * storeRegister 가게등록
     */
    //가게등록
    @Transactional
    public int storeRegister(int sellerIdx, PostStoreReq postStoresReq, String[] urls) {

        String query = "INSERT INTO Stores (sellerIdx,\n" +
                "                    categoryIdx,\n" +
                "                    city,\n" +
                "                    local,\n" +
                "                    town,\n" +
                "                    store_name,\n" +
                "                    business_phone,\n" +
                "                    business_email,\n" +
                "                    business_certificate_url,\n" +
                "                    seller_certificate_url,\n" +
                "                    copyaccount_url,\n" +
                "                    breakday,\n" +
                "                    store_open,\n" +
                "                    store_close,\n" +
                "                    store_phone,\n" +
                "                    store_address,\n" +
                "                    store_logo_url,\n" +
                "                    sign_url)\n" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        Object[] params = new Object[]{
                sellerIdx,
                postStoresReq.getCategoryIdx(),
                postStoresReq.getCity(),
                postStoresReq.getLocal(),
                postStoresReq.getTown(),
                postStoresReq.getStoreName(),
                postStoresReq.getBusinessPhone(),
                postStoresReq.getBusinessEmail(),
                urls[0] != null ? urls[0] : "",
                urls[1] != null ? urls[1] : "",
                urls[2] != null ? urls[2] : "",
                postStoresReq.getBreakDay(),
                postStoresReq.getStoreOpen(),
                postStoresReq.getStoreClose(),
                postStoresReq.getStorePhone(),
                postStoresReq.getStoreAddress(),
                urls[3] != null ? urls[3] : "",
                urls[4] != null ? urls[4] : ""
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "SELECT LAST_INSERT_ID();";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * storeDao - 2
     * 23.07.07 작성자 : 김성인
     * 가게정보 조회
     */
    public GetStoreInfoRes getStoreInfo(int storeIdx) {
        String query = "SELECT\n" +
                "    store_name, business_phone, business_email, breakday, " +
                "DATE_FORMAT(store_open, '%H:%i') as store_open, " +
                "DATE_FORMAT(store_close, '%H:%i') as store_close, " +
                "store_phone, store_logo_url, sign_url\n" +
                "FROM Stores\n" +
                "WHERE storeIdx = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new GetStoreInfoRes(
                        rs.getString("store_name"),
                        rs.getString("business_phone"),
                        rs.getString("business_email"),
                        rs.getString("breakday"),
                        rs.getString("store_open"),
                        rs.getString("store_close"),
                        rs.getString("store_phone"),
                        rs.getString("store_logo_url") == null ? "" : rs.getString("store_logo_url"),
                        rs.getString("sign_url") == null ? "" : rs.getString("sign_url")
                ), storeIdx);
    }

    /**
     * storeDao - 3
     * 23.07.07 작성자 : 이윤채, 김성인
     * 가게수정
     */
    public void storeUpdate(int storeIdx, PatchStoreInfoReq patchStoreInfoReq){
        String query= "UPDATE Stores " +
                "SET store_name = ?," +
                "business_phone = ?," +
                "business_email = ?," +
                "breakday = ?," +
                "store_open = ?," +
                "store_close = ?," +
                "store_phone = ? ," +
                "store_logo_url = ?," +
                "sign_url = ? " +
                "WHERE storeIdx = ?;";
        Object[] params = new Object[]{
                patchStoreInfoReq.getStoreName(),
                patchStoreInfoReq.getBusinessPhone(),
                patchStoreInfoReq.getBusinessEmail(),
                patchStoreInfoReq.getBreakDay(),
                patchStoreInfoReq.getStoreOpen(),
                patchStoreInfoReq.getStoreClose(),
                patchStoreInfoReq.getStorePhone(),
                patchStoreInfoReq.getStoreLogoUrl(),
                patchStoreInfoReq.getSignUrl(),
                storeIdx
        };
        this.jdbcTemplate.update(query, params);
    }


    public int storeAlreadyRegister(int sellerIdx) {
        String query = "SELECT EXISTS(SELECT * FROM Stores WHERE sellerIdx = ? AND (status = 'W' OR status = 'A'))";

        return this.jdbcTemplate.queryForObject(query, int.class, sellerIdx);
    }

    public void convertStoreOpen(int storeIdx) {
        String query ="UPDATE Stores\n" +
                "SET status = 'A'\n" +
                "WHERE storeIdx = ?";

        this.jdbcTemplate.update(query, storeIdx);
    }

    public StoreStartClose getStoreOpenNClose(int storeIdx) {
        String query = "SELECT\n" +
                "    DATE_FORMAT(store_open,  '%H:%i') as store_open,\n" +
                "    DATE_FORMAT(store_close,  '%H:%i') as store_close\n" +
                "FROM Stores\n" +
                "WHERE storeIdx = ?\n";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new StoreStartClose(
                        rs.getString("store_open"),
                        rs.getString("store_close")
                ), storeIdx);
    }


    //영업종료
    public int storeEnd(int storeIdx){
        String updatequery = "UPDATE TodayMenu SET status ='D' WHERE storeIdx = ?;";

        return this.jdbcTemplate.update(updatequery, storeIdx);
    }

    public int storeNameDuplicate(String storeName) {
        String duplicateCheck = "SELECT EXISTS(SELECT storeIdx FROM Stores WHERE store_name = ?)";

        return this.jdbcTemplate.queryForObject(duplicateCheck, int.class, storeName);
    }
}

