package com.iimj.resultportal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "candidate_status_aiba")
@Data
public class CandidateStatusAIBA {

    @Id
    private Integer id;  // 1–5

    @Column(nullable = false)
    private String name; // e.g., Selected, Rejected

    @Column(nullable = false)
    private String message; // Message to show on UI
}