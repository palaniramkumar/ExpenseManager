package com.reader.freshmanapp.mywallet.util;

/**
 * Created by Ramkumar on 25/01/15.
 * custom staus codes in the project
 */
public class TYPES {


    public enum TRANSACTION_TYPE {
        EXPENSE("EXPENSE"),  //calls constructor with value 3
        INCOME("INCOME"),  //calls constructor with value 2
        CASH_VAULT("CASH VAULT"), //ATM WRL
        NEUTRAL("NEUTRAL") //can be added to expense. Introduced, to avoid duplicate expense like credit card payment and credit card transactions
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
        CREDIT_CARD("CREDIT CARD"),
        DEBIT_CARD("DEBIT CARD"),
        NET_BANKING("NET BANKING"),
        CASH("CASH"); // semicolon needed when fields / methods follow

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
        APPROVED("APPROVED"),
        DELETED("DELETED"),
        PENDING("PENDING"),; // semicolon needed when fields / methods follow

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

