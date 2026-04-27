package com.iimj.resultportal.controller;

import java.util.Optional;
import java.util.function.ToLongFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.repository.CandidateRepository;

@RestController
@RequestMapping("/api")
public class TestController {



    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private CandidateRepository repository;

    @GetMapping("/test-db")
    public String testDB() {
    	Optional<Candidates> candidate= repository.findByRegistrationNo("REG1001") ;
        return "Total Records: " + repository.count();
    }
}