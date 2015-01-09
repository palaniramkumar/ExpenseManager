package com.reader.ramkumar.SMSparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ram on 04/01/2015.
 * template
 ========

 Rs.(.*?) was spent on ur HDFCBank CREDIT Card ending (.*?) on 2015-01-01:12:39:35 at (.*?).Avl bal - Rs.55163.00, curr o/s - Rs.14837.00

 An amount of Rs.(.*?) has been debited from your account  number (.*?) for (.*?) done using HDFC Bank NetBanking.

 INR (.*?) deposited to A/c No (.*?) towards NEFT Cr-CITI0000004-AON SERVICES I PL-GROSS MODEL A/C-RAMKUMAR K PALANI-CITIN14502045258 Val 31-DEC-14. Clr Bal is INR 1,04,879.86 subject to clearing.

 Thank you for using your HDFC Bank DEBIT/ATM Card ending (.*?) for Rs. (.*?) towards ATM WDL in (.*?) at (.*?) on 2015-01-02:16:04:16.

 Your BANK a/c xxxx (.*?) will be debited for Rs (.*?) towards (.*?) on 08/JAN/2015 .
 */
public class HDFC {
    final String [][] template ={
            {"Rs.(.*?) was spent on ur HDFCBank CREDIT Card ending (.*?) on (.*?) at (.*?).Avl","EXPENSE"},
            {"An amount of Rs.(.*?) has been debited from your account  number (.*?) for (.*?) done using HDFC Bank NetBanking","EXPENSE"},
            {"INR (.*?) deposited to A/c No (.*?)","EXPENSE"},
            {"Thank you for using your HDFC Bank DEBIT/ATM Card ending (.*?) for Rs. (.*?) towards ATM WDL in (.*?) at (.*?) on (.*?)","WDL"},
            {"Your BANK a/c xxxx (.*?) will be debited for Rs (.*?) towards (.*?) on (.*?)","INCOME"}
    };
    /*Amount,Account,Time,Where,Place*/
    final int [][] templateMap={
            {0,1,2,3},
            {0,1,3},
            {0,1},
            {1,0,4,3,2},
            {1,0,3,2}
    };
    String [] valueSet;
    String sms;
    public HDFC(String text){
        this.sms=text;
    }
    public String[] parseSMS(){
        for(int i=0;i<template.length;i++) {
            Pattern pattern = Pattern.compile(template[i][0]);
            Matcher matcher = pattern.matcher(sms);
            valueSet = new String[5];
            if (matcher.find()) {
                for(int j=0;j<templateMap[i].length;j++) {
                    System.out.println(matcher.group(j+1));
                    valueSet[templateMap[i][j]]=matcher.group(j+1);
                }
            }
            if(valueSet[0]!=null)break;
        }
        return valueSet;
    }
}
