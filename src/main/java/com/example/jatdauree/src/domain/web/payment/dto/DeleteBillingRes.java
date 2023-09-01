package com.example.jatdauree.src.domain.web.payment.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteBillingRes {
    @SerializedName("customer_uid")
    private String customer_uid;

    @SerializedName("pg_provider")
    private String pg_provider;

    @SerializedName("pg_id")
    private String pg_id;

    @SerializedName("card_name")
    private String card_name;

    @SerializedName("card_code")
    private String card_code;

    @SerializedName("card_number")
    private String card_number;

    @SerializedName("card_type")
    private String card_type;

    @SerializedName("customer_name")
    private String customer_name;

    @SerializedName("customer_tel")
    private String customer_tel;

    @SerializedName("customer_email")
    private String customer_email;

    @SerializedName("customer_addr")
    private String customer_addr;

    @SerializedName("customer_postcode")
    private String customer_postcode;

    @SerializedName("inserted")
    private Date inserted;

    @SerializedName("updated")
    private Date updated;
}
