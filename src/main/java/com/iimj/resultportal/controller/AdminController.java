package com.iimj.resultportal.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String dashboard(
            @RequestParam(defaultValue = "MBA") String type,
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(size = 10)
            Pageable pageable,
            Model model) {

        Page<Candidates> page;

        if (keyword == null || keyword.trim().isEmpty()) {
            //page = studentRepository.findByType(type, pageable);
        	page = candidateRepository.findAll(pageable);
        } else {
//            page = studentRepository
//                    .findByTypeAndNameContainingIgnoreCaseOrTypeAndEmailContainingIgnoreCase(
//                            type, keyword, type, keyword, pageable);
        	page = candidateRepository
                    .findByRegistrationNoContainingIgnoreCaseOrFullNameContainingIgnoreCase(keyword, keyword, pageable);
        }

        model.addAttribute("students", page.getContent());
        model.addAttribute("page", page); // 👈 important for Thymeleaf
        model.addAttribute("selectedType", type);
        model.addAttribute("keyword", keyword);

        return "admin-dashboard";
    }
    
    
//    @GetMapping("/dashboard") -- OLD One
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
    
    
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("type") String type,
                         RedirectAttributes redirectAttribute) {

        // process Excel and assign type to each record
        try {
            candidateImportService.importFromExcel(file);

        	redirectAttribute.addFlashAttribute("successMessage",
                    "Candidates uploaded successfully.");
        } catch (Exception e) {
        	redirectAttribute.addFlashAttribute("errorMessage",
                    "Failed to upload candidates: " + e.getMessage());
        }

        return "redirect:/admin/dashboard?type=" + type;
    }
    
    
   // @PostMapping("/upload-candidates") -- old code
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