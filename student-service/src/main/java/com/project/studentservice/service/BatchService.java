package com.project.studentservice.service;

import com.project.studentservice.dto.BatchRequestDTO;
import com.project.studentservice.dto.BatchResponseDTO;
import com.project.studentservice.exception.ResourceNotFoundException;
import com.project.studentservice.feignclient.client.CurriculumServiceClient;
import com.project.studentservice.feignclient.dto.DepartmentResponseDTO;
import com.project.studentservice.feignclient.dto.ProgramResponseDTO;
import com.project.studentservice.model.Batch;
import com.project.studentservice.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final BatchRepository batchRepository;
    private final CurriculumServiceClient curriculumServiceClient;

    public Batch createBatch(Batch batch) {
        String code = "";
        ResponseEntity<ProgramResponseDTO> programResponse= curriculumServiceClient.getProgramById(batch.getProgramId());
        if (!programResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Program not found");
        }
        ResponseEntity<DepartmentResponseDTO> departmentResponse= curriculumServiceClient.getDepartmentById(batch.getDepartmentId());
        if (!departmentResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Department not found");
        }

        code += programResponse.getBody().getCode();
        code += departmentResponse.getBody().getCode();
        code += String.valueOf(LocalDateTime.now().getYear()).substring(2);
        code += String.format("%02d", getBatchesByYear(LocalDateTime.now().getYear()).size() + 1);

        batch.setCode(code);
        return batchRepository.save(batch);
    }

    public Batch getBatchById(int id) {
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

    public Batch updateBatch(int id, Batch batchDetails) {
        Batch batch = getBatchById(id);
        batch.setProgramId(batchDetails.getProgramId());
        batch.setDepartmentId(batchDetails.getDepartmentId());
        batch.setCode(batchDetails.getCode());
        batch.setExpectedGradDate(batchDetails.getExpectedGradDate());
        batch.setSection(batchDetails.getSection());
        batch.setCreditCost(batchDetails.getCreditCost());
        return batchRepository.save(batch);
    }

    public void deleteBatch(int id) {
        Batch batch = getBatchById(id);
        batchRepository.delete(batch);
    }

    public Batch toEntity(BatchRequestDTO dto) {
        return Batch.builder()
                .programId(dto.getProgramId())
                .departmentId(dto.getDepartmentId())
                .code(dto.getCode())
                .section(dto.getSection())
                .creditCost(dto.getCreditCost())
                .expectedGradDate(dto.getExpectedGradDate())
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
}
