package com.project.paymentservice.service;

import com.project.paymentservice.model.Credit;
import com.project.paymentservice.model.CreditTransaction;
import com.project.paymentservice.model.CreditTransactionType;
import com.project.paymentservice.model.Payment;
import com.project.paymentservice.repository.CreditTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreditTransactionService {
    private final CreditTransactionRepository creditTransactionRepository;
    private final CreditService creditService;

    @Transactional
    public CreditTransaction recordTransaction(Long studentId, BigDecimal amount, CreditTransactionType type, Payment payment) {
        Credit credit = creditService.getOrCreateCredit(studentId);

        if (type == CreditTransactionType.CREDIT_USED && credit.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance for transaction");
        }

        CreditTransaction transaction = new CreditTransaction();
        transaction.setCredit(credit);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setPayment(payment);

        creditTransactionRepository.save(transaction);

        if (type == CreditTransactionType.CREDIT_ADDED) {
            creditService.increaseBalance(studentId, amount);
        } else {
            creditService.decreaseBalance(studentId, amount);
        }

        return transaction;
    }
}

