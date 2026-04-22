package com.iimj.resultportal.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.CandidatesAIBA;

@Repository
public interface CandidateAIBARepository extends JpaRepository<CandidatesAIBA, Long> {

    // For your main result check
    Optional<CandidatesAIBA> findByRegistrationNoAndDobAndEmail(
            String registrationNo,
            LocalDate dob,
            String email
    );

    // Optional: fetch by registration number
    Optional<CandidatesAIBA> findByRegistrationNo(String registrationNo);
    
    Page<CandidatesAIBA> findAll(Pageable pageable);

    Page<CandidatesAIBA> findByRegistrationNoContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String regNo, String name, Pageable pageable);
    
    
    @Modifying
    @Query(value = "TRUNCATE TABLE candidates_aiba", nativeQuery = true)
    void truncateTable();
}
