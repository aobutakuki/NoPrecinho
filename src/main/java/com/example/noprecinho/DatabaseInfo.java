package com.example.noprecinho;


import jakarta.persistence.*;


@Entity
@Table(name = "product_listings")
public class DatabaseInfo {

    //Listings Table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    public Long listing_id;


    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemsDatabase itemsDatabase;


    @Column(name = "supermarket_id")
    public Long supermarket_id;

    @Column(name = "store_specific_name")
    public String item_url;
    @Column(name = "price")
    public Double item_price;

    @Column(name = "is_available")
    public Boolean is_availiable;


    public String getItem_url() {
        return item_url;
    }

    public Long getSupermarket_id() {
        return supermarket_id;
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

    public void setItemAvailable(Boolean is_availiable) {
        this.is_availiable = is_availiable;
    }

    public ItemsDatabase getItemsDatabase() {
        return itemsDatabase;
    }

    public Long getListing_id() {
        return listing_id;
    }



}


