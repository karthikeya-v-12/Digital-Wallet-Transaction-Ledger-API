package com.wallet.ledger.controller;

import com.wallet.ledger.dto.TransactionRequest;
import com.wallet.ledger.dto.TransactionResponse;
import com.wallet.ledger.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> executeTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.processTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/ref/{ref}")
    public ResponseEntity<TransactionResponse> getTransactionByRef(@PathVariable String ref) {
        return ResponseEntity.ok(transactionService.getTransactionByRef(ref));
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByWallet(@PathVariable UUID walletId) {
        return ResponseEntity.ok(transactionService.getTransactionsByWallet(walletId));
    }
}
