package com.project.studentservice.controller;


import com.project.studentservice.dto.BatchRequestDTO;
import com.project.studentservice.dto.BatchResponseDTO;
import com.project.studentservice.model.Batch;
import com.project.studentservice.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batches")
public class BatchController {
    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<List<BatchResponseDTO>> getBatches(@RequestParam(required = false) Long programId,
                                                             @RequestParam(required = false) Long departmentId) {
        List<Batch> batches;

        if (programId != null && departmentId != null) {
            batches = batchService.getBatchesByDepartmentProgram(departmentId, programId);
        } else if (programId != null) {
            batches = batchService.getBatchesByProgram(programId);
        } else if (departmentId != null) {
            batches = batchService.getBatchesByDepartment(departmentId);
        } else {
            batches = batchService.getAllBatches();
        }

        return ResponseEntity.ok(batches.stream().map(batchService::toDTO).collect(Collectors.toList()));
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getBatchCount(@RequestParam(required = false) Integer year) {
        long count = batchService.getBatchCount(Optional.ofNullable(year).orElse(Year.now().getValue()));
        return ResponseEntity.ok(count);
    }
    @PostMapping
    public ResponseEntity<BatchResponseDTO> createBatch(@RequestBody BatchRequestDTO batchDTO) {
        Batch batch = batchService.createBatch(batchService.toEntity(batchDTO));
        return new ResponseEntity<>(batchService.toDTO(batch), HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<BatchResponseDTO> getBatchById(@PathVariable Long id) {
        Batch batch = batchService.getBatchById(id);
        return ResponseEntity.ok(batchService.toDTO(batch));
    }
    @GetMapping("/search")
    public ResponseEntity<List<BatchResponseDTO>> searchBatches(
            @RequestParam String codeQuery) {

        if (StringUtils.isBlank(codeQuery)) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        List<BatchResponseDTO> results = batchService.searchBatches(codeQuery.trim());
        return ResponseEntity.ok(results);
    }
    @GetMapping("/code/{code}")
    public ResponseEntity<BatchResponseDTO> getBatchByCode(@PathVariable String code) {
        Batch batch = batchService.getBatchByCode(code);
        return ResponseEntity.ok(batchService.toDTO(batch));
    }


    @PutMapping("/{id}")
    public ResponseEntity<BatchResponseDTO> updateBatch(@PathVariable Long id, @RequestBody BatchRequestDTO batchDTO) {
        Batch batch = batchService.updateBatch(id, batchService.toEntity(batchDTO));
        return ResponseEntity.ok(batchService.toDTO(batch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }
}
