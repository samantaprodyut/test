package com.iimj.resultportal.repository;

import com.iimj.resultportal.entity.CandidateStatusEBBA;
import com.iimj.resultportal.entity.CandidateStatusIPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateStatusEBBARepository extends JpaRepository<CandidateStatusEBBA, Serializable> {

	List<CandidateStatusEBBA> findAll();

	
	Optional<CandidateStatusEBBA> findById(Integer id);



}
