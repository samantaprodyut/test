package com.iimj.resultportal.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.iimj.resultportal.config.RedisLoader;
import com.iimj.resultportal.dto.CandidateDto;
import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.repository.CandidateRepository;

@Service
public class CandidateService {

	
    @Autowired
    private CandidateRepository candidatesRepository;
    
    private final RedisLoader redisLoader;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    

    public CandidateService(RedisLoader redisLoader ) {
        this.redisLoader = redisLoader;
    }

    public List<Candidates> getAllCandidates() {
        return candidatesRepository.findAll();
    }
    


	public void fetchDetailsFromRedis(String regNo) {
		
		Set<String> keys = redisTemplate.keys("candidate:*");
		if (keys != null) {
		    keys.forEach(key -> {
		        Object value = redisTemplate.opsForValue().get(key);
		        System.out.println("Key: " + key + ", Value: " + value);
		    });
		}
        CandidateDto candidate = redisLoader.getCandidate(regNo);
        System.out.println("<---->");

	}
	
	public void clearCandidateCache() {
	    Set<String> keys = redisTemplate.keys("candidate:*");
	    if (keys != null && !keys.isEmpty()) {
	        redisTemplate.delete(keys);
	        System.out.println("Deleted " + keys.size() + " candidate keys from Redis.");
	    } else {
	        System.out.println("No candidate keys found in Redis.");
	    }
	}
	
	public void loadAllCandidatesToCache() {
        List<Candidates> candidatesList = candidatesRepository.findAll();

        candidatesList.forEach(candidate -> {
            CandidateDto dto = new CandidateDto(
                    candidate.getId(),
                    candidate.getRegistrationNo(),
                    candidate.getFullName(),
                    candidate.getDob().toString(),
                    candidate.getEmail()
            );

            String key = "candidate:" + candidate.getRegistrationNo();
            redisTemplate.opsForValue().set(key, dto, 24, TimeUnit.HOURS); // TTL optional
        });

        System.out.println("Loaded " + candidatesList.size() + " candidates into Redis!");
    }

}
