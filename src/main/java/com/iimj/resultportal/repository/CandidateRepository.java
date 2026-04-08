package com.iimj.resultportal.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.Candidates;

@Repository
public interface CandidateRepository extends JpaRepository<Candidates, Long> {

    // For your main result check
    Optional<Candidates> findByRegistrationNoAndDobAndEmail(
            String registrationNo,
            LocalDate dob,
            String email
    );

    // Optional: fetch by registration number
    Optional<Candidates> findByRegistrationNo(String registrationNo);
    
    Page<Candidates> findAll(Pageable pageable);

    Page<Candidates> findByRegistrationNoContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String regNo, String name, Pageable pageable);
}
