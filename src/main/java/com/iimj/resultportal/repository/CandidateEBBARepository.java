package com.iimj.resultportal.repository;

import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.entity.CandidatesEBBA;
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
public interface CandidateEBBARepository extends JpaRepository<CandidatesEBBA, Long> {

    // For your main result check
    Optional<CandidatesIPM> findByRegistrationNoAndDobAndEmail(
            String registrationNo,
            LocalDate dob,
            String email
    );

    // Optional: fetch by registration number
    Optional<Candidates> findByRegistrationNo(String registrationNo);
    
    Page<CandidatesEBBA> findAll(Pageable pageable);

    Page<CandidatesEBBA> findByRegistrationNoContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String regNo, String name, Pageable pageable);
    
    
    @Modifying
    @Query(value = "TRUNCATE TABLE candidates_ebba", nativeQuery = true)
    void truncateTable();
}
