package com.iimj.resultportal.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // You can add custom queries if needed later
}
