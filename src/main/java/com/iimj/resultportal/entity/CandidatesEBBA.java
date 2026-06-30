package com.iimj.resultportal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "candidates_ebba")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CandidatesEBBA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_no", nullable = false, unique = true)
    private String registrationNo;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private CandidateStatusEBBA status;

    @Column(name = "amount_due")
    private BigDecimal amountDue;

    @Column(name = "payment_deadline")
    private String paymentDeadline;

    @Column(name = "is_paid")
    private Boolean isPaid = false;
    
    @Column(name = "candidate_current_status")
    private String candidateCurrentStatus;
    
//    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL)
//    private Payment payment;
    
    @Column(name = "sex")
    private String sex;

    @Column(name = "category")
    private String category;

    @Column(name = "pwd")
    private boolean isPwd;

    @Column(name = "upload_date")
    private String uploadDate;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "waiting_list_no")
    private String waitingListNo;
    
//    
//    @Transient
//    private String candidateCurrentStatusDescription;
}