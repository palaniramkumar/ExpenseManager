package com.reader.ramkumar.expensemanager.util;

/**
 * Created by Ramkumar on 25/01/15.
 */
public class TYPES {
    public enum TRANSACTION_TYPE {
        EXPENSE  ("EXPENSE"),  //calls constructor with value 3
        INCOME ("INCOME"),  //calls constructor with value 2
        CASH_VAULT("CASH VAULT")
        ; // semicolon needed when fields / methods follow

        private final String tranType;

        TRANSACTION_TYPE(String tranType) {
            this.tranType = tranType;
        }
        @Override
        public String toString() {
            return this.tranType;
        }

    }
    public enum TRANSACTION_SOURCE {
        CREDIT_CARD ("CREDIT CARD"),
        DEBIT_CARD ("DEBIT CARD"),
        NET_BANKING ("NET BANKING"),
        ATM_WITHDRAW ("ATM WITHDRAWAL"),
        CASH ("CASH")
        ; // semicolon needed when fields / methods follow

        private final String tranSrc;

        TRANSACTION_SOURCE(String tranSrc) {
            this.tranSrc = tranSrc;
        }
        @Override
        public String toString() {
            return this.tranSrc;
        }

    }
    public enum TRANSACTION_STATUS {
        APPROVED ("APPROVED"),
        DELETED ("DELETED"),
        PENDING ("PENDING"),
        ; // semicolon needed when fields / methods follow

        private final String status;

        TRANSACTION_STATUS(String tranSrc) {
            this.status = tranSrc;
        }
        @Override
        public String toString() {
            return this.status;
        }

    }

}

