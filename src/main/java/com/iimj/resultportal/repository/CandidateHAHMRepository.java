package com.iimj.resultportal.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.entity.CandidatesHAHM;

@Repository
public interface CandidateHAHMRepository extends JpaRepository<CandidatesHAHM, Long> {

    // For your main result check
    Optional<CandidatesHAHM> findByRegistrationNoAndDobAndEmail(
            String registrationNo,
            LocalDate dob,
            String email
    );

    // Optional: fetch by registration number
    Optional<CandidatesHAHM> findByRegistrationNo(String registrationNo);
    
    Page<CandidatesHAHM> findAll(Pageable pageable);

    Page<CandidatesHAHM> findByRegistrationNoContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String regNo, String name, Pageable pageable);
    
    @Modifying
    @Query(value = "TRUNCATE TABLE candidates_hahm", nativeQuery = true)
    void truncateTable();
}
