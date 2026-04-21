package com.iimj.resultportal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.repository.CandidateRepository;

@Service
public class CandidateService {

	@Autowired
	private CandidateRepository candidatesRepository;

	public List<Candidates> getAllCandidates() {
		return candidatesRepository.findAll();
	}

}
