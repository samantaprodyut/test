package com.iimj.resultportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "candidate_status_ipm")
@Data
public class CandidateStatusIPM {

    @Id
    private Integer id;  // 1–5

    @Column(nullable = false)
    private String name; // e.g., Selected, Rejected

    @Column(nullable = false)
    private String message; // Message to show on UI
}