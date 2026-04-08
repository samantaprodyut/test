package com.iimj.resultportal.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.iimj.resultportal.repository.CandidateRepository;
import com.iimj.resultportal.service.CandidateCacheService;
import com.iimj.resultportal.service.CandidateImportService;
import com.iimj.resultportal.service.CandidateService;
import com.iimj.resultportal.service.CandidateStatusService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CandidateService candidateService;
    private final CandidateStatusService candidateStatusService;
    private final CandidateImportService candidateImportService;
    private final CandidateCacheService candidateCacheService;
    
    @Autowired
    CandidateRepository candidateRepository;


    public AdminController(CandidateService candidateService, CandidateStatusService candidateStatusService, CandidateImportService candidateImportService, CandidateCacheService candidateCacheService) {
        this.candidateService = candidateService;
        this.candidateStatusService=candidateStatusService;
        this.candidateImportService = candidateImportService;
        this.candidateCacheService = candidateCacheService;
    }

    // Only renders the login page; actual auth is done by Spring Security filter chain
    @GetMapping("/login")
    public String showLoginForm() {
        return "admin-login";
    }
/* commented to test Pagination
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Candidates> students = candidateService.getAllCandidates();
        List<CandidateStatus> candidateStatus = candidateStatusService.getCandidateStatusList();
        model.addAttribute("students", students);
//        model.addAttribute("studentDetails", candidateStatus);
        return "admin-dashboard";
    }
*/    
    
    @GetMapping("/dashboard")
    public String getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Candidates> studentPage;

        if (search != null && !search.isEmpty()) {
            studentPage = candidateRepository
                    .findByRegistrationNoContainingIgnoreCaseOrFullNameContainingIgnoreCase(search, search, pageable);
        } else {
            studentPage = candidateRepository.findAll(pageable);
        }

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("search", search);

        return "admin-dashboard";
    }
    
    @PostMapping("/upload-candidates")
    public String uploadCandidates(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
        	candidateCacheService.loadCache();
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