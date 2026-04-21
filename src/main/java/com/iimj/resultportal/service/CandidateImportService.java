package com.iimj.resultportal.service;


import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.iimj.resultportal.entity.CandidateStatus;
import com.iimj.resultportal.entity.Candidates;
import com.iimj.resultportal.repository.CandidateRepository;
import com.iimj.resultportal.repository.CandidateStatusRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CandidateImportService {
	
	@PersistenceContext
	private EntityManager entityManager;

	private final CandidateRepository candidateRepository;
	private final CandidateStatusRepository candidateStatusRepository;

	public CandidateImportService(CandidateRepository candidateRepository,
			CandidateStatusRepository candidateStatusRepository) {
		this.candidateRepository = candidateRepository;
		this.candidateStatusRepository = candidateStatusRepository;
	}

	@Transactional
	public void importFromExcel(MultipartFile file) throws Exception {

	    if (file.isEmpty()) {
	        throw new IllegalArgumentException("Uploaded file is empty");
	    }
	    
	    System.out.println("<--start delete-->");
	    candidateRepository.deleteAllInBatch();
	    System.out.println("<--end delete-->");

	    final int BATCH_SIZE = 500;

	    List<Candidates> batchList = new ArrayList<>(BATCH_SIZE);

	    // Preload status (avoid DB hit per row)
	    Map<Integer, CandidateStatus> statusMap = candidateStatusRepository.findAll()
	            .stream()
	            .collect(Collectors.toMap(CandidateStatus::getId, s -> s));

	    DataFormatter formatter = new DataFormatter();
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    int processed = 0;

	    try (InputStream is = file.getInputStream();
	         Workbook workbook = WorkbookFactory.create(is)) {

	        Sheet sheet = workbook.getSheetAt(0);

	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

	            Row row = sheet.getRow(i);
	            if (row == null) continue;

	            try {
	                Candidates c = new Candidates();

	                String regNo = getCellValue(row, 0, formatter);
	                String email = getCellValue(row, 1, formatter);
	                String fullName = getCellValue(row, 2, formatter);
	                String sex = getCellValue(row, 3, formatter);
	                String category = getCellValue(row, 4, formatter);
	                LocalDate dobStr = getDateCellValue(row, 5);
	                String isPwd = getCellValue(row, 6, formatter);
	                LocalDate uploadDate = getDateCellValue(row, 7);;
	                String statusStr = getCellValue(row, 8, formatter);
	                String mobileNumber = getCellValue(row, 9, formatter);
	                String waitingList = getCellValue(row, 10, formatter);
	                String feePaid = getCellValue(row, 11, formatter);	              

	                // 🔹 Map entity
	                c.setRegistrationNo(regNo);
	                c.setEmail(email);
	                c.setFullName(fullName);
	                c.setSex(sex);
	                c.setCategory(category);
	                c.setDob(dobStr);
	                c.setAmountDue(BigDecimal.valueOf(500));
	                c.setPaymentDeadline("2026-01-01");
	                c.setIsPaid(false);
	                c.setPwd("1".equalsIgnoreCase(isPwd));
	                c.setUploadDate(uploadDate.toString());
	                c.setMobileNumber(mobileNumber);
	                c.setWaitingListNo(waitingList);

	                // 🔹 Fast status lookup
	                if (!statusStr.isEmpty()) {
	                    CandidateStatus status = statusMap.get(Integer.valueOf(statusStr));
	                    if (status == null) {
	                        throw new IllegalArgumentException("Invalid status: " + statusStr);
	                    }
	                    c.setStatus(status);
	                }

	                batchList.add(c);
	                processed++;

	                // 🔹 Batch flush
	                if (processed % BATCH_SIZE == 0) {
	                    candidateRepository.saveAll(batchList);
	                    candidateRepository.flush();

	                    entityManager.clear(); // VERY IMPORTANT
	                    batchList.clear();

	                    System.out.println("Processed: " + processed);
	                }

	            } catch (Exception rowEx) {
	                // 🔹 Log and skip bad row (don’t fail full import)
	                System.err.println("Error at row " + (i + 1) + ": " + rowEx.getMessage());
	            }
	        }

	        // 🔹 Save remaining
	        if (!batchList.isEmpty()) {
	            candidateRepository.saveAll(batchList);
	            candidateRepository.flush();
	            entityManager.clear();
	        }
	    }

	    System.out.println("Import completed. Total processed: " + processed);
	}
	
	private String getCellValue(Row row, int index, DataFormatter formatter) {
	    Cell cell = row.getCell(index);
	    if (cell == null) return "";
	    return formatter.formatCellValue(cell).trim();
	}
	
	
	private LocalDate parseDate(String value) {
	    if (value == null || value.isEmpty()) return null;

	    DateTimeFormatter[] formatters = {
	        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
	        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
	        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
	        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
	        DateTimeFormatter.ofPattern("dd MMM yyyy"),
	        DateTimeFormatter.ofPattern("dd-MMM-yyyy")
	    };

	    for (DateTimeFormatter formatter : formatters) {
	        try {
	            return LocalDate.parse(value, formatter);
	        } catch (Exception ignored) {}
	    }

	    throw new IllegalArgumentException("Invalid date format: " + value);
	}
	
	private LocalDate getDateCellValue(Row row, int index) {
	    Cell cell = row.getCell(index);
	    if (cell == null) return null;

	    try {
	        // ✅ Excel numeric date (most common & safest)
	        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
	            return cell.getLocalDateTimeCellValue().toLocalDate();
	        }

	        // ✅ String date
	        String value = cell.toString().trim();
	        if (value.isEmpty()) return null;

	        return parseDate(value);

	    } catch (Exception e) {
	        throw new IllegalArgumentException("Invalid date at column " + index + ": " + cell.toString());
	    }
}
}