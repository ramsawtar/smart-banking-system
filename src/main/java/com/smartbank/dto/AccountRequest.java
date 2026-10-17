package com.smartbank.dto;

import jakarta.validation.constraints.NotBlank;

public class AccountRequest {

    private String accountNumber;

    @NotBlank(message = "Account type is required")
    private String accountType;

    private java.math.BigDecimal initialDeposit;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public java.math.BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(java.math.BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
}