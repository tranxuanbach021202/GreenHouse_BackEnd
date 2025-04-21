package com.example.doanbe.repository;


import com.example.doanbe.document.Factor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FactorRepository extends MongoRepository<Factor, String> {
    Optional<Factor> findById(String id);
}
