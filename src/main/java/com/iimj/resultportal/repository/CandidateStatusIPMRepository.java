package com.iimj.resultportal.repository;

import com.iimj.resultportal.entity.CandidateStatusAIBA;
import com.iimj.resultportal.entity.CandidateStatusIPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateStatusIPMRepository extends JpaRepository<CandidateStatusIPM, Serializable> {

	List<CandidateStatusIPM> findAll();

	
	Optional<CandidateStatusIPM> findById(Integer id);



}
