package com.iimj.resultportal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.iimj.resultportal.controller.AdminController;
import com.iimj.resultportal.entity.*;
import com.iimj.resultportal.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class CandidateCacheService {

	 private final Map<String, Candidates> cache = new ConcurrentHashMap<>();
	 private final Map<String, CandidatesHAHM> cacheHAHM = new ConcurrentHashMap<>();
	 private final Map<String, CandidatesAIBA> cacheAIBA = new ConcurrentHashMap<>();
	private final Map<String, CandidatesIPM> cacheIPM = new ConcurrentHashMap<>();
	private final Map<String, CandidatesEBBA> cacheEBBA = new ConcurrentHashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(CandidateCacheService.class);


	    @Autowired
	    private CandidateRepository repository;
	    
	    @Autowired
	    private CandidateHAHMRepository hahmRepository;
	    
	    @Autowired
	    private CandidateAIBARepository aibaRepository;

		@Autowired
		private CandidateIPMRepository ipmRepository;

	@Autowired
	private CandidateEBBARepository ebbaRepository;

	    @PostConstruct
	    public void loadCache() {
	        long start = System.currentTimeMillis();
	        cache.clear();
	        cacheHAHM.clear();
	        List<Candidates> list = repository.findAll();
	        List<CandidatesHAHM> listHAHM = hahmRepository.findAll();
	        List<CandidatesAIBA> listAIBA = aibaRepository.findAll();
			List<CandidatesIPM> listIPM = ipmRepository.findAll();
			List<CandidatesEBBA> listEBBA = ebbaRepository.findAll();



			for (Candidates c : list) {
	            String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	            cache.put(key, c);
	        }
	        
	        
	        for (CandidatesHAHM c : listHAHM) {
	            String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	            cacheHAHM.put(key, c);
	        }
	        
	        for (CandidatesAIBA c : listAIBA) {
	            String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	            cacheAIBA.put(key, c);
	        }

			for (CandidatesIPM c : listIPM) {
				String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
				cacheIPM.put(key, c);
			}

			for (CandidatesEBBA c : listEBBA) {
				String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
				cacheEBBA.put(key, c);
			}

	        long end = System.currentTimeMillis();

			logger.info("Cache MBA loaded: {} records in {} ms", cache.size(), (end - start));
			logger.info("Cache HAHM loaded: {} records in {} ms", cacheHAHM.size(), (end - start));
			logger.info("Cache AIBA loaded: {} records in {} ms", cacheAIBA.size(), (end - start));
			logger.info("Cache IPM loaded: {} records in {} ms", cacheIPM.size(), (end - start));
			logger.info("Cache EBBA loaded: {} records in {} ms", cacheEBBA.size(), (end - start));



		}

	    // ---------- Fetch Candidates----------
	    public Candidates get(String regNo, String email, LocalDate dob) {
	        return cache.get(buildKey(regNo, email, dob));
	    }

	    // ---------- Update Candidates----------
	    public void update(Candidates c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cache.put(key, c);
	    }
	    // ---------- Remove Candidates----------
	    public void remove(Candidates c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cache.remove(key);
	    }

	    
	    // ---------- Fetch CandidatesHAHM----------
	    public CandidatesHAHM getHAHM(String regNo, String email, LocalDate dob) {
	        return cacheHAHM.get(buildKey(regNo, email, dob));
	    }

	    // ---------- Update CandidatesHAHM----------
	    public void updateHAHM(CandidatesHAHM c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cacheHAHM.put(key, c);
	    }
	    // ---------- Remove CandidatesHAHM----------
	    public void removeHAHM(CandidatesHAHM c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cacheHAHM.remove(key);
	    }

	    
	    // ---------- Fetch CandidatesAIBA----------
	    public CandidatesAIBA getAIBA(String regNo, String email, LocalDate dob) {
	        return cacheAIBA.get(buildKey(regNo, email, dob));
	    }

	    // ---------- Update CandidatesAIBA----------
	    public void updateAIBA(CandidatesAIBA c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cacheAIBA.put(key, c);
	    }
	    // ---------- Remove CandidatesAIBA----------
	    public void removeAIBA(CandidatesAIBA c) {
	        String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
	        cacheAIBA.remove(key);
	    }

		// ---------- Fetch CandidatesIPM----------
		public CandidatesIPM getIPM(String regNo, String email, LocalDate dob) {
			return cacheIPM.get(buildKey(regNo, email, dob));
		}

		// ---------- Update CandidatesIPM----------
		public void updateIPM(CandidatesIPM c) {
			String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
			cacheIPM.put(key, c);
		}
		// ---------- Remove CandidatesIPM----------
		public void removeIPM(CandidatesIPM c) {
			String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
			cacheIPM.remove(key);
		}

	// ---------- Fetch CandidatesEBBA----------
	public CandidatesIPM getEBBA(String regNo, String email, LocalDate dob) {
		return cacheIPM.get(buildKey(regNo, email, dob));
	}

	// ---------- Update CandidatesEBBA----------
	public void updateEBBA(CandidatesIPM c) {
		String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
		cacheIPM.put(key, c);
	}
	// ---------- Remove CandidatesEBBA----------
	public void removeEBBA(CandidatesIPM c) {
		String key = buildKey(c.getRegistrationNo(), c.getEmail(), c.getDob());
		cacheIPM.remove(key);
	}
	    
	    // ---------- Key Builder ----------
	    private String buildKey(String regNo, String email, LocalDate dob) {
	        return regNo + "_" + normalize(email) + "_" + dob;
	    }

	    private String normalize(String email) {
	        return email == null ? "" : email.trim().toLowerCase();
	    }
	
}
