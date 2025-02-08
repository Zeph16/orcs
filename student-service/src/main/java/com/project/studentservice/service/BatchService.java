package com.project.studentservice.service;

import com.project.studentservice.dto.BatchRequestDTO;
import com.project.studentservice.dto.BatchResponseDTO;
import com.project.studentservice.exception.OperationNotAllowedException;
import com.project.studentservice.exception.ResourceNotFoundException;
import com.project.studentservice.feignclient.client.CurriculumServiceClient;
import com.project.studentservice.feignclient.dto.DepartmentResponseDTO;
import com.project.studentservice.feignclient.dto.ProgramResponseDTO;
import com.project.studentservice.model.Batch;
import com.project.studentservice.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final BatchRepository batchRepository;
    private final CurriculumServiceClient curriculumServiceClient;

    public Batch createBatch(Batch batch) {
        ResponseEntity<ProgramResponseDTO> programResponse= curriculumServiceClient.getProgramById(batch.getProgramId());
        if (!programResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Program not found");
        }
        ResponseEntity<DepartmentResponseDTO> departmentResponse= curriculumServiceClient.getDepartmentById(batch.getDepartmentId());
        if (!departmentResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Department not found");
        }

        String batchCode = generateBatchCode(departmentResponse.getBody(), programResponse.getBody());
        batch.setCode(batchCode);
        return batchRepository.save(batch);
    }

    private String generateBatchCode(DepartmentResponseDTO departmentResponseDTO, ProgramResponseDTO programResponseDTO) {
        // Program Prefix
        String prefix = programResponseDTO.getName().equals("Graduate")
                ? "MS"
                : "DRB";

        // Department Suffix (if applicable)
        if (departmentResponseDTO.getName().equals("Software Engineering")) {
            prefix += "SE";
        } else if (departmentResponseDTO.getName().equals("Computer Science") && programResponseDTO.getName().equals("Graduate")) {
            prefix += "CS";
        }

        LocalDateTime currentDate = LocalDateTime.now();
        // Year Suffix
        String yearSuffix = String.valueOf(currentDate.getYear()).substring(2);

        // Count existing batches to determine batch number
        int batchCount = getBatchesByYear(LocalDateTime.now().getYear()).size();

        // Batch number (start at 01, increment for each batch)
        String batchNumber = String.format("%02d", batchCount + 1);

        return prefix + yearSuffix + batchNumber;
    }

    public Batch getBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
    }

    public Batch getBatchByCode(String code) {
        return batchRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
    }

    public List<Batch> getBatchesByDepartment(Long departmentId) {
        return batchRepository.findByDepartmentId(departmentId);
    }

    public List<Batch> getBatchesByYear(Integer year) {
        return batchRepository.findByCreationDateYear(year);
    }

    public List<Batch> getBatchesByProgram(Long programId) {
        return batchRepository.findByProgramId(programId);
    }

    public List<Batch> getBatchesByDepartmentProgram(Long departmentId, Long programId) {
        return batchRepository.findByDepartmentIdAndProgramId(departmentId, programId);
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Batch updateBatch(Long id, Batch batchDetails) {
        Batch existingBatch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + id));
        ResponseEntity<ProgramResponseDTO> programResponse= curriculumServiceClient.getProgramById(batchDetails.getProgramId());
        if (!programResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Program not found");
        }
        ResponseEntity<DepartmentResponseDTO> departmentResponse= curriculumServiceClient.getDepartmentById(batchDetails.getDepartmentId());
        if (!departmentResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Department not found");
        }
        existingBatch.setProgramId(batchDetails.getProgramId());
        existingBatch.setDepartmentId(batchDetails.getDepartmentId());
        String batchCode = generateBatchCode(departmentResponse.getBody(), programResponse.getBody());
        existingBatch.setCode(batchCode);
        existingBatch.setExpectedGradDate(batchDetails.getExpectedGradDate());
        existingBatch.setSection(batchDetails.getSection());
        existingBatch.setCreditCost(batchDetails.getCreditCost());
        return batchRepository.save(existingBatch);
    }

    public void deleteBatch(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch with ID " + id + " not found"));
        try {
            batchRepository.delete(batch);
        } catch (DataIntegrityViolationException ex) {
            throw new OperationNotAllowedException("Cannot delete batch. It has dependent records.");
        }
    }

    public Batch toEntity(BatchRequestDTO dto) {
        return Batch.builder()
                .programId(dto.getProgramId())
                .departmentId(dto.getDepartmentId())
                .code(dto.getCode())
                .section(dto.getSection())
                .creditCost(dto.getCreditCost())
                .expectedGradDate(dto.getExpectedGradDate())
                .creationDate(LocalDate.now())
                .build();
    }

    public BatchResponseDTO toDTO(Batch batch) {
        return BatchResponseDTO.builder()
                .id(batch.getId())
                .programId(batch.getProgramId())
                .departmentId(batch.getDepartmentId())
                .code(batch.getCode())
                .section(batch.getSection())
                .creditCost(batch.getCreditCost())
                .expectedGradDate(batch.getExpectedGradDate())
                .creationDate(batch.getCreationDate())
                .build();
    }

    public long getBatchCount(int year) {
        return batchRepository.countByCreationYear(year);
    }

    public List<BatchResponseDTO> searchBatches(String codeQuery) {
        // Sanitize input
        String sanitizedCodeQuery = sanitizeSearchTerm(codeQuery);
        // Perform search
        List<Batch> batches = batchRepository.searchBatches(sanitizedCodeQuery);

        // Map results to DTOs
        return batches.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String sanitizeSearchTerm(String term) {
        if (term == null) {
            return null;
        }
        // Remove any special characters that could be used for SQL injection
        // Keep only alphanumeric characters, spaces, and common punctuation
        return term.replaceAll("[^a-zA-Z0-9\\s\\-\\.\\,]", "").trim();
    }
}
