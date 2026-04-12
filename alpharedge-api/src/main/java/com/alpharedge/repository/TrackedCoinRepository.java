package com.alpharedge.repository;

import com.alpharedge.document.TrackedCoin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackedCoinRepository extends MongoRepository<TrackedCoin, String> {
    Optional<TrackedCoin> findByCoinId(String coinId);

    List<TrackedCoin> findByIsActiveTrue();
}
