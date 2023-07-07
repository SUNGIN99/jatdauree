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
     * storeDao - 1
     * 23.07.06 작성자 : 이윤채
     * storeRegister 가게등록
     */
    //가게등록
    @Transactional
    public int storeRegister(PostStoreReq postStoresReq) { //여기의 type을 PostStoreReq로 했었음 왜? --> PostStoreReq이 값을 storeRegister가

        String query = "INSERT INTO Stores (sellerIdx,categoryIdx,city,local,town,since_year,store_name, business_phone, business_email, business_certificate_url, seller_certificate_url, copyaccount_url, breakday, store_open, store_close, store_phone, store_address, store_logo_url, sign_url)\n" +
                "VALUES (?,?,?,?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?);";


        Object[] params = new Object[]{

                postStoresReq.getSellerIdx(),
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

