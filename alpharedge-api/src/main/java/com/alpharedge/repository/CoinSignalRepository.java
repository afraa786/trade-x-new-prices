package com.alpharedge.repository;

import com.alpharedge.document.CoinSignal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoinSignalRepository extends MongoRepository<CoinSignal, String> {
    Optional<CoinSignal> findTopByCoinIdOrderByComputedAtDesc(String coinId);
}
