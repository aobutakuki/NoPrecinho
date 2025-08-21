package com.example.noprecinho;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonBackReference
    private ItemsDatabase itemsDatabase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supermarket_id")
    private SupermarketInfo supermarket;

    @Column(name = "store_specific_name")
    public String item_url;
    @Column(name = "price")
    public Double item_price;

    @Column(name = "is_available")
    public Boolean is_availiable;


    public String getItem_url() {
        return item_url;
    }


    public void setItem_url(String item_url) {
            this.item_url = item_url;
    }

    public void setIs_availiable(Boolean is_availiable) {
        this.is_availiable = is_availiable;
    }

    public void setItemsDatabase(ItemsDatabase itemsDatabase) {
        this.itemsDatabase = itemsDatabase;
    }

    public void setSupermarket(SupermarketInfo supermarket) {
        this.supermarket = supermarket;
    }

    public void setListing_id(Long listing_id) {
        this.listing_id = listing_id;
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

    public SupermarketInfo getSupermarket() {
        return supermarket;
    }

    public ItemsDatabase getItem(){
        return itemsDatabase;
    }



}


