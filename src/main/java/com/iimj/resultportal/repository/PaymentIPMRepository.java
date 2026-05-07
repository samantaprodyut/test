package com.iimj.resultportal.repository;


import com.iimj.resultportal.entity.PaymentAIBA;
import com.iimj.resultportal.entity.PaymentIPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentIPMRepository extends JpaRepository<PaymentIPM, Long> {
    // You can add custom queries if needed later
}
