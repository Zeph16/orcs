package com.project.curriculumservice.controller;

import com.project.curriculumservice.dto.TermRequestDTO;
import com.project.curriculumservice.dto.TermResponseDTO;
import com.project.curriculumservice.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermController {
    private final TermService termService;

    @PostMapping
    public ResponseEntity<TermResponseDTO> createTerm(@RequestBody TermRequestDTO termDTO) {
        return new ResponseEntity<>(termService.createTerm(termDTO), HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TermResponseDTO>> searchTerms(@RequestParam String codeOrYearQuery) {
        return ResponseEntity.ok(termService.searchTerms(codeOrYearQuery));
    }

    @GetMapping("/{termId}")
    public ResponseEntity<TermResponseDTO> getTermById(@PathVariable Long termId) {
        return ResponseEntity.ok(termService.getTermById(termId));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TermResponseDTO> getTermByCode(@PathVariable String code) {
        return ResponseEntity.ok(termService.getTermByCode(code));
    }

    @GetMapping("/current")
    public ResponseEntity<TermResponseDTO> getCurrentTerm() {
        return ResponseEntity.ok(termService.getCurrentTerm());
    }

    @GetMapping
    public ResponseEntity<List<TermResponseDTO>> getAllTerms() {
        return ResponseEntity.ok(termService.getAllTerms());
    }

    @PutMapping("/{termId}")
    public ResponseEntity<TermResponseDTO> updateTerm(
            @PathVariable Long termId,
            @RequestBody TermRequestDTO termDTO) {
        return ResponseEntity.ok(termService.updateTerm(termId, termDTO));
    }

    @DeleteMapping("/{termId}")
    public ResponseEntity<Void> deleteTerm(@PathVariable Long termId) {
        termService.deleteTerm(termId);
        return ResponseEntity.noContent().build();
    }
}