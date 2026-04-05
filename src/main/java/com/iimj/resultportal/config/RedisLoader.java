package com.iimj.resultportal.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iimj.resultportal.dto.CandidateDto;
import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.repository.CandidateRepository;

@Component
public class RedisLoader {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CandidateRepository candidatesRepository;

    @Autowired
    public RedisLoader(RedisTemplate<String, Object> redisTemplate,
                       CandidateRepository candidatesRepository) {
        this.redisTemplate = redisTemplate;
        this.candidatesRepository = candidatesRepository;
    }

    /**
     * Load all candidates from DB into Redis using DTO
     */
    @Transactional(readOnly = true)
    public void loadAllCandidatesToCache() {
        List<Candidates> candidatesList = candidatesRepository.findAll();

        candidatesList.forEach(candidate -> {
            // Map entity to DTO
        	CandidateDto dto = new CandidateDto(
                    candidate.getId(),
                    candidate.getRegistrationNo(),
                    candidate.getFullName(),
                    candidate.getDob().toString(),
                    candidate.getEmail()
                    // add more fields as needed
            );

            // Save DTO to Redis
            String key = "candidate:" + candidate.getRegistrationNo();
            // Optional TTL: 24 hours
            redisTemplate.opsForValue().set(key, dto, 24, TimeUnit.HOURS);
        });

        System.out.println("Loaded " + candidatesList.size() + " candidates into Redis successfully!");
    }

    // Retrieve candidate DTO from Redis
    public CandidateDto getCandidate(String regNo) {
        Object obj = redisTemplate.opsForValue().get("candidate:" + regNo);
//        if (obj instanceof CandidateDto) {
//            return (CandidateDto) obj;
//        }
        
        if (obj instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) obj;
            return new CandidateDto(
                Long.valueOf(map.get("id").toString()),
                map.get("regNo").toString(),
                map.get("name").toString(),
                map.get("dob").toString(),
                map.get("email").toString()
            );
        }
        return null;
    }

    // Delete candidate from Redis
    public void deleteCandidate(Long id) {
        redisTemplate.delete("candidate:" + id);
    }
}