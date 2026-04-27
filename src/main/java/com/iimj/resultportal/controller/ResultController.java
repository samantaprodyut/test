package com.iimj.resultportal.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.iimj.resultportal.entity.CandidatesAIBA;
import com.iimj.resultportal.entity.CandidatesHAHM;
import com.iimj.resultportal.entity.Payment;
import com.iimj.resultportal.entity.PaymentAIBA;
import com.iimj.resultportal.entity.PaymentHAHM;
import com.iimj.resultportal.repository.CandidateAIBARepository;
import com.iimj.resultportal.repository.CandidateHAHMRepository;
import com.iimj.resultportal.repository.CandidateRepository;
import com.iimj.resultportal.repository.PaymentAIBARepository;
import com.iimj.resultportal.repository.PaymentHAHMRepository;
import com.iimj.resultportal.repository.PaymentRepository;
import com.iimj.resultportal.service.CandidateCacheService;
import com.iimj.resultportal.service.CandidateService;
import com.iimj.resultportal.service.CaptchaService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ResultController {

	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private CandidateHAHMRepository candidateHAHMRepository;
	
	@Autowired
	private CandidateAIBARepository candidateAIBARepository;

	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PaymentHAHMRepository paymentHAHMRepository;
	
	@Autowired
	private PaymentAIBARepository paymentAIBARepository;

	@Autowired
	private CandidateCacheService candidateCacheService;

	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Value("${recaptcha.site.key}")
	private String siteKey;

	private static final Logger logger = LoggerFactory.getLogger(ResultController.class);


	@GetMapping("/result")
	public ResponseEntity<?> checkResultNew(
	        @RequestParam("regNo") String regNo,
	        @RequestParam("email") String email,
	        @RequestParam("dob") String dob,
	        @RequestParam("captcha") String captcha,
	        @RequestParam("type") String type,
	        HttpSession session) {

		System.out.println("<--Hit-->");

		logger.info("INFO Text");
		logger.error("ERROR Text");

		// CAPTCHA validation (only here)
	    if (!validateCaptchaGarnet(session, captcha)) {
	        return ResponseEntity.ok(
	            Map.of("success", false, "message", "Invalid CAPTCHA...!!!")
	        );
	    }

	    // Fetch the result
	    Map<String, Object> result;

	    if ("MBA".equalsIgnoreCase(type)) {
	        result = getMBADetails(regNo, email, dob);
	    } else if ("HAHM".equalsIgnoreCase(type)) {
	        result = getHAHMDetails(regNo, email, dob); 
	    } else if ("AIBA".equalsIgnoreCase(type)) {
	        result = getAIBADetails(regNo, email, dob); 
	    } else {
	        return ResponseEntity.badRequest()
	                .body(Map.of("success", false, "message", "Invalid type"));
	    }

	    // decide HTTP status here
	    if (!(Boolean) result.get("success")) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
	    }

	    return ResponseEntity.ok(result);
	}
	
	private Map<String, Object> getMBADetails(String regNo, String email, String dob) {

	    Candidates c = candidateCacheService.get(regNo, email, LocalDate.parse(dob));

	    if (Objects.isNull(c)) {
	        return Map.of(
	            "success", false,
	            "message", "Candidate not found"
	        );
	    }

	    return Map.of(
	        "success", true,
	        "candidate", c,
	        "message", "Candidate details found"
	    );
	}
	
	
	
	private Map<String, Object> getHAHMDetails(String regNo, String email, String dob) {

	    CandidatesHAHM c = candidateCacheService.getHAHM(regNo, email, LocalDate.parse(dob));

	    if (Objects.isNull(c)) {
	        return Map.of(
	            "success", false,
	            "message", "Candidate not found"
	        );
	    }

	    return Map.of(
	        "success", true,
	        "candidate", c,
	        "message", "Candidate details found"
	    );
	}

	private Map<String, Object> getAIBADetails(String regNo, String email, String dob) {

	    CandidatesAIBA c = candidateCacheService.getAIBA(regNo, email, LocalDate.parse(dob));


	    if (Objects.isNull(c)) {
	        return Map.of(
	            "success", false,
	            "message", "Candidate not found"
	        );
	    }

	    return Map.of(
	        "success", true,
	        "candidate", c,
	        "message", "Candidate details found"
	    );
	}
	
	
	// Submit manual payment
	@PostMapping("/payment")
	public ResponseEntity<Map<String, Object>> savePayment(@RequestBody Map<String, Object> payload) {
		String regNo = (String) payload.get("registrationNo");
		String email = (String) payload.get("email");
		String dob = (String) payload.get("dob");
		String trxId = (String) payload.get("transactionId");
		String bankName = (String) payload.get("bankName");
		Double amount = Double.valueOf(payload.get("amount").toString());
		String type = (String) payload.get("type");
		Map<String, Object> resp = new ConcurrentHashMap<>();
		if ("MBA".equalsIgnoreCase(type)) {
			resp = postPayMBA(regNo, email, dob, trxId, bankName, amount);
		} else if ("HAHM".equalsIgnoreCase(type)) {
			resp = postPayHAHM(regNo, email, dob, trxId, bankName, amount);
		} else if ("AIBA".equalsIgnoreCase(type)) {
			resp = postPayAIBA(regNo, email, dob, trxId, bankName, amount);
		}
		return ResponseEntity.ok(resp);
	}

	private Map<String, Object> postPayAIBA(String regNo, String email, String dob, String trxId, String bankName,
			Double amount) {
		CandidatesAIBA c = candidateCacheService.getAIBA(regNo, email, LocalDate.parse(dob));

		Map<String, Object> resp = new HashMap<>();

		if (!Objects.isNull(c)) {
			// Save payment
			PaymentAIBA payment = new PaymentAIBA();
			payment.setCandidate(c);
			payment.setCandidateRegId(c.getRegistrationNo());
			payment.setTrxId(trxId);
			payment.setBankName(bankName);
			payment.setAmount(BigDecimal.valueOf(amount));
			paymentAIBARepository.save(payment);

			// Update candidate
			c.setIsPaid(true);
			c.setCandidateCurrentStatus("102");
			CandidatesAIBA updatedCandidate = candidateAIBARepository.save(c);

			// Update the cache by removing the old and updating the new
			updateAIBACache(updatedCandidate);
			resp.put("candidate", c);
			resp.put("success", true);
			resp.put("fullName", c.getFullName());
			resp.put("message",
					"Dear " + c.getFullName() + ", Your payment details are submitted, subject to verification.");
		} else {
			resp.put("success", false);
			resp.put("fullName", regNo);
			resp.put("message", "Candidate not found, payment cannot be recorded.");
		}
		return resp;
	}
	
	private Map<String, Object> postPayMBA(String regNo, String email, String dob, String trxId, String bankName,
			Double amount) {
		Candidates c = candidateCacheService.get(regNo, email, LocalDate.parse(dob));

		Map<String, Object> resp = new HashMap<>();

		if (!Objects.isNull(c)) {
			// Save payment
			Payment payment = new Payment();
			payment.setCandidate(c);
			payment.setCandidateRegId(c.getRegistrationNo());
			payment.setTrxId(trxId);
			payment.setBankName(bankName);
			payment.setAmount(BigDecimal.valueOf(amount));
			paymentRepository.save(payment);

			// Update candidate
			c.setIsPaid(true);
			c.setCandidateCurrentStatus("102");
			Candidates updatedCandidate = candidateRepository.save(c);

			// Update the cache by removing the old and updating the new
			updateCache(updatedCandidate);
			resp.put("candidate", c);
			resp.put("success", true);
			resp.put("fullName", c.getFullName());
			resp.put("message",
					"Dear " + c.getFullName() + ", Your payment details are submitted, subject to verification.");
		} else {
			resp.put("success", false);
			resp.put("fullName", regNo);
			resp.put("message", "Candidate not found, payment cannot be recorded.");
		}
		return resp;
	}

	private Map<String, Object> postPayHAHM(String regNo, String email, String dob, String trxId, String bankName,
			Double amount) {
		CandidatesHAHM c = candidateCacheService.getHAHM(regNo, email, LocalDate.parse(dob));

		Map<String, Object> resp = new HashMap<>();

		if (!Objects.isNull(c)) {

			// Save payment
			PaymentHAHM payment = new PaymentHAHM();
			payment.setCandidate(c);
			payment.setCandidateRegId(c.getRegistrationNo());
			payment.setTrxId(trxId);
			payment.setBankName(bankName);
			payment.setAmount(BigDecimal.valueOf(amount));
			paymentHAHMRepository.save(payment);

			// Update candidate
			c.setIsPaid(true);
			c.setCandidateCurrentStatus("102");
			CandidatesHAHM updatedCandidate = candidateHAHMRepository.save(c);

			// Update the cache by removing the old and updating the new
			updateHAHMCache(updatedCandidate);
			resp.put("candidate", c);
			resp.put("success", true);
			resp.put("fullName", c.getFullName());
			resp.put("message",
					"Dear " + c.getFullName() + ", Your payment details are submitted, subject to verification.");
		} else {
			resp.put("success", false);
			resp.put("fullName", regNo);
			resp.put("message", "Candidate not found, payment cannot be recorded.");
		}
		return resp;
	}


	public boolean validateCaptchaGarnet(HttpSession session, String input) {

		String sessionId = session.getId();

		String key = "CAPTCHA:" + sessionId;

		String storedCaptcha = redisTemplate.opsForValue().get(key);

		if (storedCaptcha == null) {
			return false;
		}

		boolean valid = storedCaptcha.equalsIgnoreCase(input);

		if (valid) {
			redisTemplate.delete(key); // one-time use
		}

		return valid;
	}

	
	private boolean validateCaptcha(HttpSession session, String captcha) {
	    String sessionCaptcha = (String) session.getAttribute("captcha");

	    if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(captcha)) {
	        return false;
	    }

	    session.removeAttribute("captcha");
	    return true;
	}

	/*
	 * Method to update the cache post payment for MBA.
	 */
	private void updateCache(Candidates c) {
		candidateCacheService.remove(c);
		candidateCacheService.update(c);
	}

	/*
	 * Method to update the cache post payment for HAHM.
	 */
	private void updateHAHMCache(CandidatesHAHM c) {
		candidateCacheService.removeHAHM(c);
		candidateCacheService.updateHAHM(c);
	}
	
	
	/*
	 * Method to update the cache post payment for AIBA.
	 */
	private void updateAIBACache(CandidatesAIBA c) {
		
		candidateCacheService.removeAIBA(c);
		candidateCacheService.updateAIBA(c);
	}
	
}