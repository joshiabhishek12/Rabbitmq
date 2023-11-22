package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelFileReader {
    private static final Logger logger = LogManager.getLogger(ExcelFileReader.class);
//
//    @Value("${rabbitmq.virtual-host}")
//    private String rabbitmqVirtualHost;
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    
    public void readDataFromExcel(String filePath, String accountId, String accountInstance) throws IOException {
        FileInputStream excelFile = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        String queueName = "dynamic_queue_" + System.currentTimeMillis();
     //   String queueName = rabbitmqVirtualHost + "." + "dynamic_queue_" + System.currentTimeMillis();

        amqpAdmin.declareQueue(new org.springframework.amqp.core.Queue(queueName));
        logger.info("Dynamic Queue Name: " + queueName);

        jdbcTemplate.update("INSERT INTO dataimport (Column_1) VALUES (?)", queueName);
//       amqpAdmin.deleteQueue(queueName);
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            List<String> rowData = new ArrayList<>();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowData.add(cell.toString());
            }

            rowData.add ("Account ID: " + accountId);
            rowData.add("Account Instance: " + accountInstance);

            rabbitTemplate.convertAndSend(queueName, rowData);
        }

        excelFile.close();
    }
}
