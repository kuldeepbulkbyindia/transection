package com.bbi.transactionsp2.exceptions;

public class TransactionException extends RuntimeException{
    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }
}
