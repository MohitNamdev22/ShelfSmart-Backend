package com.shelfsmart.shelfsmart_backend.controller;

import com.shelfsmart.shelfsmart_backend.model.StockMovement;
import com.shelfsmart.shelfsmart_backend.repository.StockMovementRepository;
import com.shelfsmart.shelfsmart_backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/daily")
    public ResponseEntity<byte[]> getDailyReport() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<StockMovement> movements = stockMovementRepository.findByTimestampBetween(startOfDay, endOfDay);
        String csv = generateCsv(movements);
        return createCsvResponse(csv, "daily-report-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv");
    }

    @GetMapping("/weekly")
    public ResponseEntity<byte[]> getWeeklyReport() {
        LocalDateTime startOfWeek = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().plusDays(1).atStartOfDay();
        List<StockMovement> movements = stockMovementRepository.findByTimestampBetween(startOfWeek, endOfWeek);
        String csv = generateCsv(movements);
        return createCsvResponse(csv, "weekly-report-" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv");
    }

    @GetMapping("/custom")
    public ResponseEntity<byte[]> getCustomReport(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(endDateStr).plusDays(1).atStartOfDay(); // Include end date
        List<StockMovement> movements = stockMovementRepository.findByTimestampBetween(startDate, endDate);
        String csv = generateCsv(movements);
        String filename = "custom-report-" + startDateStr + "-to-" + endDateStr + ".csv";
        return createCsvResponse(csv, filename);
    }

    private String generateCsv(List<StockMovement> movements) {
        StringBuilder csv = new StringBuilder("MovementId,ItemId,ItemName,QuantityChanged,MovementType,Timestamp\n");
        for (StockMovement movement : movements) {
            String itemName = inventoryService.getItemById(movement.getItemId())
                    .map(item -> "\"" + item.getName() + "\"")
                    .orElse("Unknown");
            csv.append(movement.getMovementId()).append(",")
                    .append(movement.getItemId()).append(",")
                    .append(itemName).append(",")
                    .append(movement.getQuantityChanged()).append(",")
                    .append(movement.getMovementType()).append(",")
                    .append(movement.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        }
        return csv.toString();
    }

    private ResponseEntity<byte[]> createCsvResponse(String csv, String filename) {
        byte[] csvBytes = csv.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(csvBytes.length);
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }
}