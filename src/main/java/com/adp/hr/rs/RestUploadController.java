package com.adp.hr.rs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.adp.hr.HRScoreCardConfiguration;
import com.adp.hr.utils.ErrorCellVO;
import com.adp.hr.utils.ExcelFileProcessor;

@RestController
public class RestUploadController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileProcessor.class);

	private static final String UPLOADED_FOLDER = "C:\\temp";

	@Autowired
	private HRScoreCardConfiguration hrScoreCardConfiguration;

	@PostMapping("/api/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadfile) {

		FileValidationResponse response = new FileValidationResponse();
		try {
			String filePath = this.saveUploadedFiles(uploadfile);
			ExcelFileProcessor fileProcessor = new ExcelFileProcessor(filePath, hrScoreCardConfiguration);
			fileProcessor.processFile();
			response.setFileName(filePath);
			response.setUiErrorCells(fileProcessor.getUiErrorCells());
		} catch (IOException e) {
			return new ResponseEntity<>("Input File in not Valid", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@GetMapping("/api/test")
	public ResponseEntity<?> uploadFile() {

		FileValidationResponse response = new FileValidationResponse();
		try {
			String filePath = "C://Users//rayudura//Desktop//BaseFile.xlsx";
			ExcelFileProcessor fileProcessor = new ExcelFileProcessor(filePath, hrScoreCardConfiguration);
			fileProcessor.processFile();
			response.setFileName(filePath);
			response.setUiErrorCells(fileProcessor.getUiErrorCells());
		} catch (IOException e) {
			return new ResponseEntity<>("Input File in not Valid", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// save file
	private String saveUploadedFiles(MultipartFile file) throws IOException {
		String filePath = UPLOADED_FOLDER + File.separator + UUID.randomUUID() + "_" + file.getOriginalFilename();
		byte[] bytes = file.getBytes();
		// Generate some random unique number and append before the file name to avoid
		// the duplicate
		// file names
		Path path = Paths.get(filePath);
		Files.write(path, bytes);
		return filePath;

	}

	public static class FileValidationResponse {

		private Map<String, List<List<ErrorCellVO>>> uiErrorCells;

		private String fileName;

		public Map<String, List<List<ErrorCellVO>>> getUiErrorCells() {
			return uiErrorCells;
		}

		public void setUiErrorCells(Map<String, List<List<ErrorCellVO>>> uiErrorCells) {
			this.uiErrorCells = uiErrorCells;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}
}
