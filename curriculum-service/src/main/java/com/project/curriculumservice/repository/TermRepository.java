package com.project.curriculumservice.repository;

import com.project.curriculumservice.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByCode(String code);

    @Query("SELECT t FROM Term t WHERE LOWER(t.code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(t.academicYear AS STRING) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Term> searchTerms(@Param("query") String codeOrYearQuery);
}
