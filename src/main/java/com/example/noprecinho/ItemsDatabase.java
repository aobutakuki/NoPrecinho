package com.example.noprecinho;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "itemsDatabase", fetch = FetchType.EAGER)
    private List<DatabaseInfo> listings;

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

    public List<DatabaseInfo> getListings() {
        return listings;
    }

    public double getLowestPrice() {
        if (listings == null || listings.isEmpty()) {
            return Double.MAX_VALUE; // Return a very high number if there are no listings
        }

        return listings.stream()
                .filter(listing -> Objects.nonNull(listing.getIs_availiable()) && listing.getIs_availiable()) // Filter for available listings
                .mapToDouble(DatabaseInfo::getItem_price) // Get the price of each
                .min() // Find the minimum price
                .orElse(Double.MAX_VALUE); // Return a high number if no available listings are found
    }
}
