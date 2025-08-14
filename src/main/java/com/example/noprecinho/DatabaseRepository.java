package com.example.noprecinho;
import org.hibernate.boot.model.relational.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatabaseRepository extends JpaRepository<DatabaseInfo, Long> {


    //Long item(ItemsDatabase item);
    @Query("SELECT d FROM DatabaseInfo d WHERE d.itemsDatabase.item_id IN :itemIds")
    List<DatabaseInfo> findListingsByItemIds(@Param("itemIds") List<Long> itemIds);

}
