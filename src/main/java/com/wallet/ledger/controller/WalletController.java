package com.wallet.ledger.controller;

import com.wallet.ledger.dto.CreateWalletRequest;
import com.wallet.ledger.dto.WalletResponse;
import com.wallet.ledger.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable UUID id) {
        return ResponseEntity.ok(walletService.getWalletById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletResponse>> getWalletsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(walletService.getWalletsByUserId(userId));
    }
}
