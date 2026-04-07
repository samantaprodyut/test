package com.iimj.resultportal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.repository.CandidateRepository;

import jakarta.annotation.PostConstruct;

@Service
public class CandidateCacheService {

	 private final Map<String, Candidates> cache = new ConcurrentHashMap<>();

	    @Autowired
	    private CandidateRepository repository;

	    @PostConstruct
	    public void loadCache() {
	        long start = System.currentTimeMillis();
	        cache.clear();
	        List<Candidates> list = repository.findAll();

	        for (Candidates c : list) {
	            String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	            cache.put(key, c);
	        }

	        long end = System.currentTimeMillis();
	        System.out.println("Cache loaded: " + cache.size() + " records in " + (end - start) + " ms");
	    }

	    // ---------- Fetch ----------
	    public Candidates get(String regNo, String email, LocalDate dob) {
	        return cache.get(buildKey(regNo, email, dob));
	    }

	    // ---------- Update ----------
	    public void update(Candidates c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cache.put(key, c);
	    }

	    public void remove(Candidates c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cache.remove(key);
	    }

	    // ---------- Key Builder ----------
	    private String buildKey(String regNo, String email, LocalDate dob) {
	        return regNo + "_" + normalize(email) + "_" + dob;
	    }

	    private String normalize(String email) {
	        return email == null ? "" : email.trim().toLowerCase();
	    }
	
}
