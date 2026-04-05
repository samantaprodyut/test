package com.iimj.resultportal.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDto {
	
	private Long id;
	private String regNo;
    private String name;
    private String dob;
    private String email;
    // Add any other primitive fields
}