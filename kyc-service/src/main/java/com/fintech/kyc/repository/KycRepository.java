package com.fintech.kyc.repository;

import com.fintech.kyc.model.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycRepository extends JpaRepository<Kyc, String> {
    Optional<Kyc> findByUserId(String userId);
}
