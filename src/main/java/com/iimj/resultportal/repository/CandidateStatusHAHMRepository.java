package com.iimj.resultportal.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.CandidateStatusHAHM;

@Repository
public interface CandidateStatusHAHMRepository extends JpaRepository<CandidateStatusHAHM, Serializable> {

	List<CandidateStatusHAHM> findAll();

	
	Optional<CandidateStatusHAHM> findById(Integer id);



}
