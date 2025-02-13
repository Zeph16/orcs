package com.project.paymentservice.service;

import com.project.paymentservice.model.Credit;
import com.project.paymentservice.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final CreditRepository creditRepository;

    @Transactional
    public Credit getOrCreateCredit(Long studentId) {
        return creditRepository.findByStudentId(studentId)
                .orElseGet(() -> {
                    Credit credit = new Credit();
                    credit.setStudentId(studentId);
                    credit.setBalance(BigDecimal.ZERO);
                    return creditRepository.save(credit);
                });
    }

    @Transactional
    public void increaseBalance(Long studentId, BigDecimal amount) {
        creditRepository.increaseBalanceByStudentId(studentId, amount);
    }

    @Transactional
    public void decreaseBalance(Long studentId, BigDecimal amount) {
        creditRepository.decreaseBalanceByStudentId(studentId, amount);
    }
}
