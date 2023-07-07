package com.example.jatdauree.src.domain.store.dao;

import com.example.jatdauree.src.domain.store.dto.PostStoreReq;
import com.example.jatdauree.src.domain.store.dto.PostStoreUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;


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
    public int storeRegister(int sellerIdx, PostStoreReq postStoresReq) {

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
                postStoresReq.getBusinessCertificateUrl(),
                postStoresReq.getSellerCertificateUrl(),
                postStoresReq.getCopyAccountUrl(),
                postStoresReq.getBreakDay(),
                postStoresReq.getStoreOpen(),
                postStoresReq.getStoreClose(),
                postStoresReq.getStorePhone(),
                postStoresReq.getStoreAddress(),
                postStoresReq.getStoreLogoUrl(),
                postStoresReq.getSignUrl(),
        };

        this.jdbcTemplate.update(query, params);

        String lastInsertIdQuery = "SELECT LAST_INSERT_ID();";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * storeDao - 2
     * 23.07.06 작성자 : 이윤채
     * storeUpdate 가게수정
     */
    public int storeUpdate(PostStoreUpdateReq postStoreUpdateReq){
        String query= "UPDATE Stores SET store_name =?,business_phone =?,business_email=?,breakday =?,store_open=?,store_close=?,store_phone=?,store_logo_url=?,sign_url=? WHERE sellerIdx=? AND storeIdx=?;";
        Object[] params = new Object[]{

                postStoreUpdateReq.getStoreName(),
                postStoreUpdateReq.getBusinessPhone(),
                postStoreUpdateReq.getBusinessEmail(),
                postStoreUpdateReq.getBreakDay(),
                postStoreUpdateReq.getStoreOpen(),
                postStoreUpdateReq.getStoreClose(),
                postStoreUpdateReq.getStorePhone(),
                postStoreUpdateReq.getStoreLogoUrl(),
                postStoreUpdateReq.getSignUrl(),
                postStoreUpdateReq.getSellerIdx(),
                postStoreUpdateReq.getStoreIdx()

        };
        return this.jdbcTemplate.update(query, params);

        //String lastInsertIdQuery = "SELECT LAST_INSERT_ID();";
        // return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }


}

