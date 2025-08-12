package com.example.noprecinho;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class ItemsDatabase{

    @Id
    @Column(name = "item_id")
    public Long item_id;

    @Column(name = "item_name")
    public String item_name;

    @Column(name = "base_name")
    public String base_name;

    @Column(name = "image_link")
    public String image_link;

    public Long getItem_id() {
        return item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_base_name() {
        return base_name;
    }

    public String getImage_link() {
        return image_link;
    }
}
