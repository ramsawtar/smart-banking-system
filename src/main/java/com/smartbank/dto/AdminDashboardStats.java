package com.smartbank.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminDashboardStats {

    private long totalCustomers;
    private long totalAccounts;
    private long activeAccounts;
    private long blockedAccounts;
    private BigDecimal totalBankBalance;
    private long totalTransactions;
    private long totalDeposits;
    private long totalWithdrawals;
    private long totalTransfers;
    private List<TransactionResponse> recentTransactions;

    public AdminDashboardStats() {
    }

    public AdminDashboardStats(long totalCustomers, long totalAccounts, long activeAccounts,
                               long blockedAccounts, BigDecimal totalBankBalance, long totalTransactions,
                               long totalDeposits, long totalWithdrawals, long totalTransfers,
                               List<TransactionResponse> recentTransactions) {
        this.totalCustomers = totalCustomers;
        this.totalAccounts = totalAccounts;
        this.activeAccounts = activeAccounts;
        this.blockedAccounts = blockedAccounts;
        this.totalBankBalance = totalBankBalance;
        this.totalTransactions = totalTransactions;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.totalTransfers = totalTransfers;
        this.recentTransactions = recentTransactions;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public long getActiveAccounts() {
        return activeAccounts;
    }

    public void setActiveAccounts(long activeAccounts) {
        this.activeAccounts = activeAccounts;
    }

    public long getBlockedAccounts() {
        return blockedAccounts;
    }

    public void setBlockedAccounts(long blockedAccounts) {
        this.blockedAccounts = blockedAccounts;
    }

    public BigDecimal getTotalBankBalance() {
        return totalBankBalance;
    }

    public void setTotalBankBalance(BigDecimal totalBankBalance) {
        this.totalBankBalance = totalBankBalance;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public long getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(long totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public long getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(long totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public long getTotalTransfers() {
        return totalTransfers;
    }

    public void setTotalTransfers(long totalTransfers) {
        this.totalTransfers = totalTransfers;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
