package com.project.studentservice.repository;

import com.project.studentservice.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByCardId(String cardId);
    List<Student> findByBatchId(int batchId);
    List<Student> findByBatchSection(char section);
}
