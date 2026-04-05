package com.iimj.resultportal.controller;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iimj.resultportal.entity.CandidateStatus;
import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.entity.Payment;
import com.iimj.resultportal.repository.CandidateRepository;
import com.iimj.resultportal.repository.PaymentRepository;
import com.iimj.resultportal.service.CandidateService;
import com.iimj.resultportal.service.CaptchaService;

import jakarta.servlet.http.HttpSession;



@RestController
@RequestMapping("/api")
public class ResultController {

    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private CaptchaService captchaService; 
    
    @Value("${recaptcha.site.key}")
    private String siteKey;
    

    @GetMapping("/result")
    public ResponseEntity<?> checkResult(
            @RequestParam("regNo") String regNo,
            @RequestParam("email") String email,
            @RequestParam("dob") String dob,
            @RequestParam("captcha") String captcha,
            @RequestParam("captchaToken") String captchaToken,
            HttpSession session) {
        
//    	candidateService.fetchDetailsFromRedis(regNo);
//    	
//    	candidateService.clearCandidateCache();
//    	
//    	candidateService.fetchDetailsFromRedis(regNo);
    	// 1 typed image captcha validation
        String sessionCaptcha = (String) session.getAttribute("captcha");

        if (sessionCaptcha == null ||
                !sessionCaptcha.equalsIgnoreCase(captcha)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid typed captcha"));
        }
    	 // Step 1: Verify captcha first
    	String sessionCaptchaNew = (String) session.getAttribute("captcha");

    	if (sessionCaptchaNew == null ||
    	        !sessionCaptchaNew.equalsIgnoreCase(captcha)) {
    	    return ResponseEntity.badRequest()
    	            .body(Map.of("message", "Invalid typed captcha"));
    	}


    	
        boolean isCaptchaValid = captchaService.verifyCaptcha(captchaToken);

        if (!isCaptchaValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Captcha validation failed"));
        }
    	
        Optional<Candidates> candidateOpt = candidateRepository.findByRegistrationNoAndDobAndEmail(regNo, dob, email);

        if (candidateOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Candidate not found"));
        }

        Candidates candidate = candidateOpt.get();
        CandidateStatus status = candidate.getStatus(); // from DB

        Map<String, Object> response = new HashMap<>();
        response.put("registrationNo", candidate.getRegistrationNo());
        response.put("fullName", candidate.getFullName());
        response.put("statusId", status.getId());
        response.put("statusName", status.getName());
        response.put("message", status.getMessage());

        // Case-based behavior using status.id
        switch (status.getId()) {
            case 1: // Selected
                response.put("amountDue", candidate.getAmountDue());
                response.put("paymentDeadline", candidate.getPaymentDeadline());
                response.put("isPaid", candidate.getIsPaid());
                response.put("showPaymentForm", true); // SPA can render Pay Now form
                response.put("alreadyPaid", candidate.getIsPaid());
                break;
            case 2: // Waitlisted
            case 3: // Rejected
            case 4: // Documents Pending
            case 5: // Payment Pending
                response.put("showPaymentForm", false);
                break;
            default:
                response.put("showPaymentForm", false);
        }

        return ResponseEntity.ok(response);
    }


    // Submit manual payment
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> savePayment(@RequestBody Map<String, Object> payload) {
        String regNo = (String) payload.get("regNo");
        String trxId = (String) payload.get("trxId");
        String bankName = (String) payload.get("bankName");
        Double amount = Double.valueOf(payload.get("amount").toString());

        Optional<Candidates> optCandidate = candidateRepository.findByRegistrationNo(regNo);
        Map<String, Object> resp = new HashMap<>();

        if (optCandidate.isPresent()) {
            Candidates c = optCandidate.get();

            // Save payment
            Payment payment = new Payment();
            payment.setCandidate(c);
            payment.setTrxId(trxId);
            payment.setBankName(bankName);
            payment.setAmount(BigDecimal.valueOf(amount));
            paymentRepository.save(payment);

            // Update candidate
            c.setIsPaid(true);
            candidateRepository.save(c);
            redisTemplate.opsForValue().set(regNo, c);


            resp.put("fullName", c.getFullName());
            resp.put("message", "Your payment details are submitted, subject to verification.");
        } else {
            resp.put("fullName", "Candidate");
            resp.put("message", "Candidate not found, payment cannot be recorded.");
        }

        return ResponseEntity.ok(resp);
    }

}