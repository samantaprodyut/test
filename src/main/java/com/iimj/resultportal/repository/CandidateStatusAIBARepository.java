package com.iimj.resultportal.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.CandidateStatusAIBA;

@Repository
public interface CandidateStatusAIBARepository extends JpaRepository<CandidateStatusAIBA, Serializable> {

	List<CandidateStatusAIBA> findAll();

	
	Optional<CandidateStatusAIBA> findById(Integer id);



}
