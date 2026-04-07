package com.iimj.resultportal.controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iimj.resultportal.entity.CandidateStatus;
import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.service.CandidateImportService;
import com.iimj.resultportal.service.CandidateService;
import com.iimj.resultportal.service.CandidateStatusService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CandidateService candidateService;
    private final CandidateStatusService candidateStatusService;
    private final CandidateImportService candidateImportService;



    public AdminController(CandidateService candidateService, CandidateStatusService candidateStatusService, CandidateImportService candidateImportService) {
        this.candidateService = candidateService;
        this.candidateStatusService=candidateStatusService;
        this.candidateImportService = candidateImportService;
    }

    // Only renders the login page; actual auth is done by Spring Security filter chain
    @GetMapping("/login")
    public String showLoginForm() {
        return "admin-login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Candidates> students = candidateService.getAllCandidates();
        List<CandidateStatus> candidateStatus = candidateStatusService.getCandidateStatusList();
        model.addAttribute("students", students);
//        model.addAttribute("studentDetails", candidateStatus);
        return "admin-dashboard";
    }
    
    @PostMapping("/upload-candidates")
    public String uploadCandidates(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            candidateImportService.importFromExcel(file);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Candidates uploaded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to upload candidates: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // No /admin/logout handler needed; Spring Security handles it
}