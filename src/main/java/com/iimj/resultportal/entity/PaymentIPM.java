package com.iimj.resultportal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments_ipm")
@Data
public class PaymentIPM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidatesIPM candidate;
    
    @Column(name = "candidate_reg_id", nullable = false)
    private String candidateRegId;

    @Column(name = "trx_id", nullable = false)
    private String trxId;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();
}