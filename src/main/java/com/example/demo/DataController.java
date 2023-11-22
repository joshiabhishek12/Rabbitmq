package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;




@RestController
public class DataController {
    private static final Logger logger = LogManager.getLogger(DataController.class);

    @Autowired
    private ExcelFileReader excelFileReader;
    

    @PostMapping(value = "/process-excel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String processExcelAndSendToRabbitMQ(
            @RequestPart(value = "file", required = true) MultipartFile file,
            @RequestPart(value = "accountid", required = true) String accountId,
            @RequestPart(value = "accountinstance", required = true) String accountInstance) {
        try {
            Path tempFile = Files.createTempFile("temp-", ".xlsx");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            excelFileReader.readDataFromExcel(tempFile.toString(), accountId, accountInstance);

            Files.delete(tempFile);

            return "Data sent to RabbitMQ successfully";
        } catch (IOException e) {
        	logger.error("Error in excel file {}", e.getMessage());

            return "Error processing Excel file: " + e.getMessage();
        } catch (Exception e) {
        	logger.error("Unexcepted error in excel file {}", e.getMessage());

            return "Unexpected error: " + e.getMessage();
        }
    }

@GetMapping("/test")
public String test() {
	return "test";
}
@PostMapping("/test1")
public String test1() {
	return "test";
}



}