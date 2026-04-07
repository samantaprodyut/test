package com.iimj.resultportal.service;


import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

@Service
public class CandidateImportService {

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

		// 1) Truncate existing data
//		candidateRepository.deleteAllInBatch(); // or a native TRUNCATE if you prefer[web:95][web:98][web:101]

		List<Candidates> toSave = new ArrayList<>();

		try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) { // handles
																										// XLSX[web:93][web:102]
			Sheet sheet = workbook.getSheetAt(0);

			// Assume first row is header; start at row 1
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				final int index = i;

				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				Candidates c = new Candidates();

				// Adapt column indexes to your actual Excel layout
				String regNo = getStringCell(row, 0);
				String email = getStringCell(row, 1);
				String fullName = getStringCell(row, 2);
				String sex = getStringCell(row, 3);
				String category = getStringCell(row, 4);
				String dob = getDateCell(row, 5); // e.g. yyyy-MM-dd or text 2000-01-15
				String isPwd = getStringCell(row, 6);
				String uploadDate = getDateCell(row, 7); // e.g. yyyy-MM-dd or text 2000-01-15
				String status = getStringCell(row, 8);
				String mobileNumber = getStringCell(row, 9);
				String waitingList = getStringCell(row, 10);
				String feePaid = getStringCell(row, 11);

				c.setRegistrationNo(regNo); // 0
				c.setEmail(email); // 1
				c.setFullName(fullName); // 2
				c.setSex(sex);// 3
				c.setCategory(category);// 4
				
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate date = LocalDate.parse(dob, inputFormatter);
				c.setDob(date);// 5
				c.setAmountDue(BigDecimal.valueOf(500));
				c.setPaymentDeadline("2026-01-01");
				c.setIsPaid(false); // or derive from Excel if you have a column
				c.setPwd("1".equalsIgnoreCase(isPwd) ? true : false);
				c.setUploadDate(uploadDate);
				// Resolve status: by code or name depending on your table
				CandidateStatus statusInDb = candidateStatusRepository.findById(Integer.valueOf(status)).orElseThrow(
						() -> new IllegalArgumentException("Unknown status '" + status + "' at row " + (index + 1)));
				c.setStatus(statusInDb);// 8
				c.setMobileNumber(mobileNumber);// 9
				c.setWaitingListNo(waitingList);// 10
//c.setIsPaid(feePaid);//11
				toSave.add(c);
			}
		}

		candidateRepository.saveAll(toSave);
		System.out.println("<------------END----->");
	}

	private String getStringCell(Row row, int idx) {
		Cell cell = row.getCell(idx);
		if (cell == null)
			return null;
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue().trim();
	}
	
	private String getDateCell(Row row, int idx) {
	    Cell cell = row.getCell(idx);
	    if (cell == null) return "";

	    if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
	        Date date = cell.getDateCellValue();

	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        return sdf.format(date);
	    }

	    return "";
	}

}