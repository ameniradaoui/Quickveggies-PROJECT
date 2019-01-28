package com.quickveggies.entities;

/**
 *
 * @author Sergey Orlov <serg.merlin@gmail.com>
 */
public class AccountEntryPayment {
    
    private Long id;
    private Long accountEntryId;
    private String paymentTable;
    private Long paymentId;

    public AccountEntryPayment(Long id, Long accountEntryId, String paymentTable, Long paymentId) {
        this.id = id;
        this.accountEntryId = accountEntryId;
        this.paymentTable = paymentTable;
        this.paymentId = paymentId;
    }
    
    public AccountEntryPayment() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountEntryId() {
        return accountEntryId;
    }

    public void setAccountEntryId(Long accountEntryId) {
        this.accountEntryId = accountEntryId;
    }

    public String getPaymentTable() {
        return paymentTable;
    }

    public void setPaymentTable(String paymentTable) {
        this.paymentTable = paymentTable;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
