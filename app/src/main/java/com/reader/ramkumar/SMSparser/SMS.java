package com.reader.ramkumar.SMSparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ram on 04/01/2015.
 */
public class SMS {
    public String address;
    public String text;
    public String mobileNo;
    public String id;

    public String trans_type;
    public String bankName;
    public String amount;
    public String where;
    public String when;
    public String place;
    public String account;
    public boolean findSMS(){
        if(text.contains("HDFC")) {
            bankName="HDFC";
            HDFC bank=new HDFC(text);
            String [] strParse = bank.parseSMS();
            amount=strParse[0];
            where = strParse[3];
            //when = strParse[2];
            place = strParse[4];
            account=strParse[1];
            return true;
        }
        else
            return false;
    }
    void parseSMS(){
        if(text.contains("deposited") || text.contains("DEPOSITED") ){
            trans_type="credit";
        }
        else if(text.contains("debited") || text.contains("DEBITED") ){
            trans_type="debit";
        }
        amount=getAmount();
        where=getTenent();
    }
    String getAmount(){
        Pattern pattern = Pattern.compile("Rs(.*?).");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println(matcher.group(1));
        }
        return "";
    }
    String getTenent(){
        Pattern pattern = Pattern.compile("at (.*?) on");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
    // code -> deposited , debitted

}
