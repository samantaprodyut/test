package com.iimj.resultportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "candidates")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Candidates {

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
   // @JsonFormat(pattern = "yyyy-MM-dd")
    private String dob;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private CandidateStatus status;

    @Column(name = "amount_due")
    private BigDecimal amountDue;

    @Column(name = "payment_deadline")
    private String paymentDeadline;

    @Column(name = "is_paid")
    private Boolean isPaid = false;

    @OneToOne(mappedBy = "candidate", cascade = CascadeType.ALL)
    private Payment payment;
}