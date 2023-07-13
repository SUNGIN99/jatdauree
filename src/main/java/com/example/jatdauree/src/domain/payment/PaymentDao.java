package com.example.jatdauree.src.domain.payment;

import com.example.jatdauree.src.domain.payment.iamportDto.BillingKeyFoundation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Repository
public class PaymentDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Transactional
    public int checkExsitBillingKey(String newCustomerUid) {
        return 0;
    }

    @Transactional
    public int issueBilling(BillingKeyFoundation billingKeyFoundation) {
        return 0;
    }
}
