package com.iimj.resultportal.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iimj.resultportal.entity.Payment;
import com.iimj.resultportal.entity.PaymentHAHM;

@Repository
public interface PaymentHAHMRepository extends JpaRepository<PaymentHAHM, Long> {
    // You can add custom queries if needed later
}
