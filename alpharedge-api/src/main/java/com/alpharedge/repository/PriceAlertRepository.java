package com.alpharedge.repository;

import com.alpharedge.document.PriceAlert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceAlertRepository extends MongoRepository<PriceAlert, String> {
    List<PriceAlert> findByUserIdAndIsActiveTrue(String userId);

    List<PriceAlert> findByIsActiveTrueAndIsTriggeredFalse();

    Optional<PriceAlert> findByIdAndUserId(String id, String userId);
}
