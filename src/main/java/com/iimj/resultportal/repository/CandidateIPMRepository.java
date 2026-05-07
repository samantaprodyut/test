package com.iimj.resultportal.repository;

import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.entity.CandidatesIPM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CandidateIPMRepository extends JpaRepository<CandidatesIPM, Long> {

    // For your main result check
    Optional<CandidatesIPM> findByRegistrationNoAndDobAndEmail(
            String registrationNo,
            LocalDate dob,
            String email
    );

    // Optional: fetch by registration number
    Optional<Candidates> findByRegistrationNo(String registrationNo);
    
    Page<CandidatesIPM> findAll(Pageable pageable);

    Page<CandidatesIPM> findByRegistrationNoContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String regNo, String name, Pageable pageable);
    
    
    @Modifying
    @Query(value = "TRUNCATE TABLE candidates_ipm", nativeQuery = true)
    void truncateTable();
}
