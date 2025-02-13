package com.project.paymentservice.repository;
import com.project.paymentservice.model.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

    Optional<Credit> findByStudentId(Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE Credit c SET c.balance = c.balance + :amount WHERE c.studentId = :studentId")
    void increaseBalanceByStudentId(@Param("studentId") Long studentId, @Param("amount") BigDecimal amount);

    @Modifying
    @Transactional
    @Query("UPDATE Credit c SET c.balance = c.balance - :amount WHERE c.studentId = :studentId")
    void decreaseBalanceByStudentId(@Param("studentId") Long studentId, @Param("amount") BigDecimal amount);

}