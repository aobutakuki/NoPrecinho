package com.example.noprecinho;

import org.hibernate.boot.model.relational.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsRepository extends JpaRepository<ItemsDatabase, Long> {

}
