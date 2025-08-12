package com.example.noprecinho;
import org.hibernate.boot.model.relational.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatabaseRepository extends JpaRepository<DatabaseInfo, Long> {



}
