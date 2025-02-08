package com.project.studentservice.repository;


import com.project.studentservice.model.Batch;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    Optional<Batch> findByCode(String code);
    List<Batch> findByDepartmentId(Long departmentId);
    List<Batch> findByProgramId(Long programId);
    List<Batch> findByDepartmentIdAndProgramId(Long departmentId, Long programId);

    @Query("SELECT b FROM Batch b WHERE YEAR(b.creationDate) = :year")
    List<Batch> findByCreationDateYear(int year);
    @Query("SELECT COUNT(b) FROM Batch b WHERE YEAR(b.creationDate) = :year")
    long countByCreationYear(@Param("year") int year);
    @Query("SELECT b FROM Batch b WHERE LOWER(b.code) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Batch> searchBatches(@Param("query") String query);
}