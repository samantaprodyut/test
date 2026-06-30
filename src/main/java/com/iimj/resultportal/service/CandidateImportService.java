package com.iimj.resultportal.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.iimj.resultportal.controller.AdminController;
import com.iimj.resultportal.entity.*;
import com.iimj.resultportal.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CandidateImportService {

	private static final Logger logger = LoggerFactory.getLogger(CandidateImportService.class);


	@PersistenceContext
	private EntityManager entityManager;

	private final CandidateRepository candidateRepository;
	private final CandidateHAHMRepository candidateHAHMRepository;
	private final CandidateAIBARepository candidateAIBARepository;
	private final CandidateIPMRepository candidateIPMRepository;
	private final CandidateEBBARepository candidateEBBARepository;

	private final CandidateStatusRepository candidateStatusRepository;
	private final CandidateStatusHAHMRepository candidateStatusHAHMRepository;
	private final CandidateStatusAIBARepository candidateStatusAIBARepository;
	private final CandidateStatusIPMRepository candidateStatusIPMRepository;
	private final CandidateStatusEBBARepository candidateStatusEBBARepository;


	public CandidateImportService(CandidateRepository candidateRepository,
                                  CandidateStatusRepository candidateStatusRepository,
                                  CandidateStatusHAHMRepository candidateStatusHAHMRepository,
                                  CandidateHAHMRepository candidateHAHMRepository,
                                  CandidateAIBARepository candidateAIBARepository,
                                  CandidateStatusAIBARepository candidateStatusAIBARepository,
                                  CandidateIPMRepository candidateIPMRepository, CandidateEBBARepository candidateEBBARepository,
                                  CandidateStatusIPMRepository candidateStatusIPMRepository,
                                  CandidateStatusEBBARepository candidateStatusEBBARepository) {


		this.candidateRepository = candidateRepository;
		this.candidateStatusRepository = candidateStatusRepository;
		this.candidateStatusHAHMRepository = candidateStatusHAHMRepository;
		this.candidateHAHMRepository = candidateHAHMRepository;
		this.candidateAIBARepository=candidateAIBARepository;
		this.candidateStatusAIBARepository=candidateStatusAIBARepository;
		this.candidateIPMRepository=candidateIPMRepository;
        this.candidateEBBARepository = candidateEBBARepository;
        this.candidateStatusIPMRepository=candidateStatusIPMRepository;
		this.candidateStatusEBBARepository=candidateStatusEBBARepository;
	}

	@Transactional
	public void importFromExcel(MultipartFile file, String type) throws Exception {

		if (file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file is empty");
		}

		final int BATCH_SIZE = 500;
		DataFormatter formatter = new DataFormatter();
		int processed = 0;

		try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {

			Sheet sheet = workbook.getSheetAt(0);

			// ========================= MBA =========================
			if ("MBA".equalsIgnoreCase(type)) {

//                candidateRepository.deleteAllInBatch();
				candidateRepository.truncateTable();
				Map<Integer, CandidateStatus> statusMap = candidateStatusRepository.findAll().stream()
						.collect(Collectors.toMap(CandidateStatus::getId, s -> s));

				List<Candidates> batchList = new ArrayList<>(BATCH_SIZE);

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {

					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					try {
						Candidates c = mapMBA(row, formatter);

						String statusStr = getCellValue(row, 8, formatter);
						if (!statusStr.isEmpty()) {
							CandidateStatus status = statusMap.get(Integer.valueOf(statusStr));
							if (status == null) {
								throw new IllegalArgumentException("Invalid status: " + statusStr);
							}
							c.setStatus(status);
						}

						batchList.add(c);
						processed++;

						if (batchList.size() == BATCH_SIZE) {
							flushMBA(batchList);
						}

					} catch (Exception ex) {
						logger.error("Error at row " + (i + 1) + ": {}",ex.getMessage());
					}
				}

				flushMBA(batchList);
			}

			// ========================= HAHM =========================
			else if ("HAHM".equalsIgnoreCase(type)) {

				candidateHAHMRepository.truncateTable();

				Map<Integer, CandidateStatusHAHM> statusMap = candidateStatusHAHMRepository.findAll().stream()
						.collect(Collectors.toMap(CandidateStatusHAHM::getId, s -> s));

				List<CandidatesHAHM> batchList = new ArrayList<>(BATCH_SIZE);

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {

					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					try {
						CandidatesHAHM c = mapHAHM(row, formatter);

						String statusStr = getCellValue(row, 8, formatter);
						if (!statusStr.isEmpty()) {
							CandidateStatusHAHM status = statusMap.get(Integer.valueOf(statusStr));
							if (status == null) {
								throw new IllegalArgumentException("Invalid status: " + statusStr);
							}
							c.setStatus(status);
						}

						batchList.add(c);
						processed++;

						if (batchList.size() == BATCH_SIZE) {
							flushHAHM(batchList);
						}

					} catch (Exception ex) {
						logger.error("Error at row " + (i + 1) + ": {}",ex.getMessage());
					}
				}

				flushHAHM(batchList);
			}

			// ========================= EBBA =========================
			else if ("EBBA".equalsIgnoreCase(type)) {

				candidateEBBARepository.truncateTable();

				Map<Integer, CandidateStatusEBBA> statusMap = candidateStatusEBBARepository.findAll().stream()
						.collect(Collectors.toMap(CandidateStatusEBBA::getId, s -> s));

				List<CandidatesEBBA> batchList = new ArrayList<>(BATCH_SIZE);

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {

					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					try {
						CandidatesEBBA c = mapEBBA(row, formatter);

						String statusStr = getCellValue(row, 8, formatter);
						if (!statusStr.isEmpty()) {
							CandidateStatusEBBA status = statusMap.get(Integer.valueOf(statusStr));
							if (status == null) {
								throw new IllegalArgumentException("Invalid status: " + statusStr);
							}
							c.setStatus(status);
						}

						batchList.add(c);
						processed++;

						if (batchList.size() == BATCH_SIZE) {
							flushEBBA(batchList);
						}

					} catch (Exception ex) {
						logger.error("Error at row " + (i + 1) + ": {}",ex.getMessage());
					}
				}

				flushEBBA(batchList);
			}
			// ========================= IPM =========================
			else if ("IPM".equalsIgnoreCase(type)) {

				candidateIPMRepository.truncateTable();

				Map<Integer, CandidateStatusIPM> statusMap = candidateStatusIPMRepository.findAll().stream()
						.collect(Collectors.toMap(CandidateStatusIPM::getId, s -> s));

				List<CandidatesIPM> batchList = new ArrayList<>(BATCH_SIZE);

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {

					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					try {
						CandidatesIPM c = mapIPM(row, formatter);

						String statusStr = getCellValue(row, 8, formatter);
						if (!statusStr.isEmpty()) {
							CandidateStatusIPM status = statusMap.get(Integer.valueOf(statusStr));
							if (status == null) {
								throw new IllegalArgumentException("Invalid status: " + statusStr);
							}
							c.setStatus(status);
						}

						batchList.add(c);
						processed++;

						if (batchList.size() == BATCH_SIZE) {
							flushIPM(batchList);
						}

					} catch (Exception ex) {
						logger.error("Error at row " + (i + 1) + ": {}",ex.getMessage());
					}
				}

				flushIPM(batchList);
			}
			// ========================= AIBA =========================

			else if ("AIBA".equalsIgnoreCase(type)) {

				candidateAIBARepository.truncateTable();
				Map<Integer, CandidateStatusAIBA> statusMap = candidateStatusAIBARepository.findAll().stream()
						.collect(Collectors.toMap(CandidateStatusAIBA::getId, s -> s));

				List<CandidatesAIBA> batchList = new ArrayList<>(BATCH_SIZE);

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {

					Row row = sheet.getRow(i);
					if (row == null)
						continue;

					try {
						CandidatesAIBA c = mapAIBA(row, formatter);

						String statusStr = getCellValue(row, 8, formatter);
						if (!statusStr.isEmpty()) {
							CandidateStatusAIBA status = statusMap.get(Integer.valueOf(statusStr));
							if (status == null) {
								throw new IllegalArgumentException("Invalid status: " + statusStr);
							}
							c.setStatus(status);
						}

						batchList.add(c);
						processed++;

						if (batchList.size() == BATCH_SIZE) {
							flushAIBA(batchList);
						}

					} catch (Exception ex) {
						logger.error("Error at row " + (i + 1) + ": {}",ex.getMessage());
					}
				}

				flushAIBA(batchList);
			}
			logger.info("Import completed. Total processed: {}",processed);
		}
	}

	// ===================== MBA MAPPER =====================
	private Candidates mapMBA(Row row, DataFormatter formatter) {

		Candidates c = new Candidates();

		c.setRegistrationNo(getCellValue(row, 0, formatter));
		c.setEmail(getCellValue(row, 1, formatter));
		c.setFullName(getCellValue(row, 2, formatter));
		c.setSex(getCellValue(row, 3, formatter));
		c.setCategory(getCellValue(row, 4, formatter));
		c.setDob(getDateCellValue(row, 5));

		c.setAmountDue(BigDecimal.valueOf(500));
		c.setPaymentDeadline("2026-01-01");
		c.setIsPaid(false);

		c.setPwd("1".equalsIgnoreCase(getCellValue(row, 6, formatter)));

		LocalDate uploadDate = getDateCellValue(row, 7);
		c.setUploadDate(uploadDate != null ? uploadDate.toString() : null);

		c.setMobileNumber(getCellValue(row, 9, formatter));
		c.setWaitingListNo(getCellValue(row, 10, formatter));

		return c;
	}

	// ===================== HAHM MAPPER =====================
	private CandidatesHAHM mapHAHM(Row row, DataFormatter formatter) {

		CandidatesHAHM c = new CandidatesHAHM();

		c.setRegistrationNo(getCellValue(row, 0, formatter));
		c.setEmail(getCellValue(row, 1, formatter));
		c.setFullName(getCellValue(row, 2, formatter));
		c.setSex(getCellValue(row, 3, formatter));
		c.setCategory(getCellValue(row, 4, formatter));
		c.setDob(getDateCellValue(row, 5));

		c.setAmountDue(BigDecimal.valueOf(500));
		c.setPaymentDeadline("2026-01-01");
		c.setIsPaid(false);

		c.setPwd("1".equalsIgnoreCase(getCellValue(row, 6, formatter)));

		LocalDate uploadDate = getDateCellValue(row, 7);
		c.setUploadDate(uploadDate != null ? uploadDate.toString() : null);

		c.setMobileNumber(getCellValue(row, 9, formatter));
		c.setWaitingListNo(getCellValue(row, 10, formatter));

		return c;
	}

	// ===================== IPM MAPPER =====================
	private CandidatesIPM mapIPM(Row row, DataFormatter formatter) {

		CandidatesIPM c = new CandidatesIPM();

		c.setRegistrationNo(getCellValue(row, 0, formatter));
		c.setEmail(getCellValue(row, 1, formatter));
		c.setFullName(getCellValue(row, 2, formatter));
		c.setSex(getCellValue(row, 3, formatter));
		c.setCategory(getCellValue(row, 4, formatter));
		c.setDob(getDateCellValue(row, 5));

		c.setAmountDue(BigDecimal.valueOf(500));
		c.setPaymentDeadline("2026-01-01");
		c.setIsPaid(false);

		c.setPwd("1".equalsIgnoreCase(getCellValue(row, 6, formatter)));

		LocalDate uploadDate = getDateCellValue(row, 7);
		c.setUploadDate(uploadDate != null ? uploadDate.toString() : null);

		c.setMobileNumber(getCellValue(row, 9, formatter));
		c.setWaitingListNo(getCellValue(row, 10, formatter));

		return c;
	}

	// ===================== EBBA MAPPER =====================
	private CandidatesEBBA mapEBBA(Row row, DataFormatter formatter) {

		CandidatesEBBA c = new CandidatesEBBA();

		c.setRegistrationNo(getCellValue(row, 0, formatter));
		c.setEmail(getCellValue(row, 1, formatter));
		c.setFullName(getCellValue(row, 2, formatter));
		c.setSex(getCellValue(row, 3, formatter));
		c.setCategory(getCellValue(row, 4, formatter));
		c.setDob(getDateCellValue(row, 5));

		c.setAmountDue(BigDecimal.valueOf(500));
		c.setPaymentDeadline("2026-01-01");
		c.setIsPaid(false);

		c.setPwd("1".equalsIgnoreCase(getCellValue(row, 6, formatter)));

		LocalDate uploadDate = getDateCellValue(row, 7);
		c.setUploadDate(uploadDate != null ? uploadDate.toString() : null);

		c.setMobileNumber(getCellValue(row, 9, formatter));
		c.setWaitingListNo(getCellValue(row, 10, formatter));

		return c;
	}
	// ===================== AIBA MAPPER =====================
		private CandidatesAIBA mapAIBA(Row row, DataFormatter formatter) {

			CandidatesAIBA c = new CandidatesAIBA();

			c.setRegistrationNo(getCellValue(row, 0, formatter));
			c.setEmail(getCellValue(row, 1, formatter));
			c.setFullName(getCellValue(row, 2, formatter));
			c.setSex(getCellValue(row, 3, formatter));
			c.setCategory(getCellValue(row, 4, formatter));
			c.setDob(getDateCellValue(row, 5));

			c.setAmountDue(BigDecimal.valueOf(500));
			c.setPaymentDeadline("2026-01-01");
			c.setIsPaid(false);

			c.setPwd("1".equalsIgnoreCase(getCellValue(row, 6, formatter)));

			LocalDate uploadDate = getDateCellValue(row, 7);
			c.setUploadDate(uploadDate != null ? uploadDate.toString() : null);

			c.setMobileNumber(getCellValue(row, 9, formatter));
			c.setWaitingListNo(getCellValue(row, 10, formatter));

			return c;
		}

	// ===================== FLUSH METHODS =====================
	private void flushMBA(List<Candidates> batchList) {
		if (batchList.isEmpty())
			return;

		candidateRepository.saveAll(batchList);
		candidateRepository.flush();
		entityManager.clear();
		batchList.clear();
	}

	private void flushHAHM(List<CandidatesHAHM> batchList) {
		if (batchList.isEmpty())
			return;

		candidateHAHMRepository.saveAll(batchList);
		candidateHAHMRepository.flush();
		entityManager.clear();
		batchList.clear();
	}

	private void flushIPM(List<CandidatesIPM> batchList) {
		if (batchList.isEmpty())
			return;

		candidateIPMRepository.saveAll(batchList);
		candidateIPMRepository.flush();
		entityManager.clear();
		batchList.clear();
	}

	private void flushEBBA(List<CandidatesEBBA> batchList) {
		if (batchList.isEmpty())
			return;

		candidateEBBARepository.saveAll(batchList);
		candidateEBBARepository.flush();
		entityManager.clear();
		batchList.clear();
	}
	
	private void flushAIBA(List<CandidatesAIBA> batchList) {
		if (batchList.isEmpty())
			return;

		candidateAIBARepository.saveAll(batchList);
		candidateAIBARepository.flush();
		entityManager.clear();
		batchList.clear();
	}

	// ===================== UTIL =====================
	private String getCellValue(Row row, int index, DataFormatter formatter) {
		Cell cell = row.getCell(index);
		if (cell == null)
			return "";
		return formatter.formatCellValue(cell).trim();
	}

	private LocalDate getDateCellValue(Row row, int index) {
		Cell cell = row.getCell(index);
		if (cell == null)
			return null;

		try {
			if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
				return cell.getLocalDateTimeCellValue().toLocalDate();
			}

			String value = cell.toString().trim();
			if (value.isEmpty())
				return null;

			return parseDate(value);

		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date at column " + index + ": " + cell);
		}
	}

	private LocalDate parseDate(String value) {
		try {
			return LocalDate.parse(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format: " + value);
		}
	}
}