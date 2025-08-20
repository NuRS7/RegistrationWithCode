package com.auth.registration.repository;

import com.auth.registration.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByCodeAndEmail(String code, String email);

    void deleteByEmail(String email);

    List<VerificationCode> findByEmail(String email);


}
