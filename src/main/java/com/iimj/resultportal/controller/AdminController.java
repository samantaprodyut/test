package com.iimj.resultportal.controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.service.CandidateService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CandidateService candidateService;

    public AdminController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    // Only renders the login page; actual auth is done by Spring Security filter chain
    @GetMapping("/login")
    public String showLoginForm() {
        return "admin-login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Candidates> students = candidateService.getAllCandidates();
        model.addAttribute("students", students);
        return "admin-dashboard";
    }

    // No /admin/logout handler needed; Spring Security handles it
}