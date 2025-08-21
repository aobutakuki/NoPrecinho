package com.example.noprecinho;

import org.hibernate.boot.model.relational.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemsRepository extends JpaRepository<ItemsDatabase, Long> {

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END FROM ItemsDatabase i WHERE i.base_name = :baseName")
    boolean existsByBase_name(@Param("baseName") String baseName);

    @Query("SELECT i FROM ItemsDatabase i WHERE i.base_name = :baseName")
    Optional<ItemsDatabase> findByBase_name(@Param("baseName") String baseName);
}
