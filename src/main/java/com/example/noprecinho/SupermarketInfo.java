package com.example.noprecinho;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "supermarkets")
public class SupermarketInfo {

    @Id
    @Column(name = "supermarket_id")
    private Long supermarket_id;

    @Column(name = "supermarket_name")
    private String supermarket_name;

    @Column(name = "logo_filename")
    private String logo_filename;

    public Long getSupermarket_id() {
        return supermarket_id;
    }

    public String getSupermarket_name() {
        return supermarket_name;
    }

    public String getLogo_filename() {
        return logo_filename;
    }


}
