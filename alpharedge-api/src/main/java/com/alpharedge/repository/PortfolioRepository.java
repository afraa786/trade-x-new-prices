package com.alpharedge.repository;

import com.alpharedge.document.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {
    List<Portfolio> findByUserId(String userId);

    Optional<Portfolio> findByIdAndUserId(String id, String userId);
}
