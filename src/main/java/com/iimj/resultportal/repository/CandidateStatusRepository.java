package com.iimj.resultportal.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.CandidateStatus;

@Repository
public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Serializable> {

	List<CandidateStatus> findAll();

	
	Optional<CandidateStatus> findById(Integer id);



}
