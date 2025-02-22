package com.example.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByPhoneNumber(String phoneNumber);
    @Query("update wallet w set w.balance = w.balance + ?2 where w.phoneNumber = ?")
    void updateWallet(String phoneNumber, Double amount);
}
