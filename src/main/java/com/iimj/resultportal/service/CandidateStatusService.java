package com.iimj.resultportal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iimj.resultportal.entity.CandidateStatus;
import com.iimj.resultportal.repository.CandidateStatusRepository;

@Service
public class CandidateStatusService {
	
	@Autowired
	CandidateStatusRepository candidateStatusRepository;
	
	public List<CandidateStatus> getCandidateStatusList(){
		return candidateStatusRepository.findAll();
	}

}
