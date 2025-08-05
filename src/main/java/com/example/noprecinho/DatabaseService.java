package com.example.noprecinho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseRepository databaseRepository;
    public DatabaseRepository getDatabaseRepository() {
        return databaseRepository;
    }

    public String getURLbyId(Long id){
        return databaseRepository.findById(id).map(databaseInfo -> databaseInfo.getItem_url()).orElseThrow(() -> new RuntimeException("Entry not found with id: " + id));
    }
}
