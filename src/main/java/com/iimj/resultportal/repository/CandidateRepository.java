package com.iimj.resultportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.Candidates;

import java.time.LocalDate;
import java.util.Optional;

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
}
