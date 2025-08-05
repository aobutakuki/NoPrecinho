package com.example.noprecinho;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class DatabaseInfo {

    //Listings Table
    @Id
    @Column(name = "item_id")
    public Long item_id;

    @Column(name = "store_specific_name")
    public String item_url;
    @Column(name = "price")
    public Double item_price;

    @Column(name = "is_available")
    public Boolean is_availiable;


    public String getItem_url() {
        return item_url;
    }

    public Double getItem_price() {
        return item_price;
    }

    public void setItem_price(Double item_price) {
        this.item_price = item_price;
    }

    public Boolean getIs_availiable() {
        return is_availiable;
    }

    public Long getItem_id() {
        return item_id;
    }

}
