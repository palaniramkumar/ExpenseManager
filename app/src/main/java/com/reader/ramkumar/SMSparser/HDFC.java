package com.reader.ramkumar.SMSparser;

import com.reader.ramkumar.expensemanager.util.TYPES;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ram on 04/01/2015.
 * template
 ========

 Rs.(.*?) was spent on ur HDFCBank CREDIT Card ending (.*?) on 2015-01-01:12:39:35 at (.*?).Avl bal - Rs.1234.00, curr o/s - Rs.123.00

 An amount of Rs.(.*?) has been debited from your account  number (.*?) for (.*?) done using HDFC Bank NetBanking.

 INR (.*?) deposited to A/c No (.*?) towards NEFT Cr-CITI0000004-AON SERVICES I PL-GROSS MODEL A/C-RAMKUMAR K PALANI-CITIN14502045258 Val 31-DEC-14. Clr Bal is INR (.*?) subject to clearing.

 Thank you for using your HDFC Bank DEBIT/ATM Card ending (.*?) for Rs. (.*?) towards ATM WDL in (.*?) at (.*?) on 2015-01-02:16:04:16.

 Your BANK a/c xxxx (.*?) will be debited for Rs (.*?) towards (.*?) on 08/JAN/2015 .
 */
public class HDFC {
    public SMSParserData parserValue;

    public class  SMSParserData{
        public String valueSet[];
        public String trans_type;
        public String trans_src;
    }
    final String [][] template ={
            {"Rs.(.*?) was spent on ur HDFCBank CREDIT Card ending (.*?) on (.*?) at (.*?).Avl", TYPES.TRANSACTION_TYPE.EXPENSE.toString(),TYPES.TRANSACTION_SOURCE.CREDIT_CARD.toString()},
            {"An amount of Rs.(.*?) has been debited from your account  number (.*?) for (.*?) done using HDFC Bank NetBanking",TYPES.TRANSACTION_TYPE.EXPENSE.toString(),TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"INR (.*?) deposited to A/c No (.*?)",TYPES.TRANSACTION_TYPE.INCOME.toString(),TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"Thank you for using your HDFC Bank DEBIT/ATM Card ending (.*?) for Rs. (.*?) towards ATM WDL in (.*?) at (.*?) on (.*?)",TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(),TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"Your BANK a/c xxxx (.*?) will be debited for Rs (.*?) towards (.*?) on (.*?)",TYPES.TRANSACTION_TYPE.EXPENSE.toString(),TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()}
    };
    /*Amount,Account,Time,Where,Place*/
    final int [][] templateMap={
            {0,1,2,3},
            {0,1,3},
            {0,1},
            {1,0,4,3,2},
            {1,0,3,2}
    };

    String sms;
    public HDFC(String text){
        this.sms=text;
    }
    public SMSParserData parseSMS(){
        parserValue = new SMSParserData();
        for(int i=0;i<template.length;i++) {
            Pattern pattern = Pattern.compile(template[i][0]);
            Matcher matcher = pattern.matcher(sms);
            parserValue.valueSet = new String[5];
            if (matcher.find()) {
                for(int j=0;j<templateMap[i].length;j++) {
                    System.out.println(matcher.group(j+1));
                    parserValue.valueSet[templateMap[i][j]]=matcher.group(j+1);
                }
            }
            if(parserValue.valueSet[0]!=null){
                parserValue.trans_src= template[i][2];
                parserValue.trans_type= template[i][1];
                break;
            }
        }
        return parserValue;
    }
}

