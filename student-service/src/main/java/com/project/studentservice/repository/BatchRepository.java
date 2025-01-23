package com.project.studentservice.repository;


import com.project.studentservice.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Integer> {
    Optional<Batch> findByCode(String code);
    List<Batch> findByDepartmentId(Long departmentId);
    List<Batch> findByProgramId(Long programId);
    List<Batch> findByDepartmentIdAndProgramId(Long departmentId, Long programId);

    @Query("SELECT b FROM Batch b WHERE YEAR(b.creationDate) = :year")
    List<Batch> findByCreationDateYear(int year);
}