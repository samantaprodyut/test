package com.iimj.resultportal.dto;

import lombok.Data;

@Data
public class CandidateStatusDto {

    
    private Integer id;  // 1–5

    private String name; // e.g., Selected, Rejected

    private String message; // Message to show on UI

}
