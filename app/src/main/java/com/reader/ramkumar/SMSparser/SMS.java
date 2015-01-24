package com.reader.ramkumar.SMSparser;


/**
 * Created by Ram on 04/01/2015.
 */
public class SMS {
    public String address;
    public String text;
    public String card_type;
    public String id;

    public String trans_type;
    public String bankName;
    public String amount;
    public String where;
    public String when;
    public String place;
    public String account;
    public String expanse_type;
    public boolean findSMS(){
        if(text.contains("HDFC")) {
            bankName="HDFC";
            HDFC bank=new HDFC(text);
            HDFC.SMSParserData smsparsedata = bank.parseSMS();
            amount=smsparsedata.valueSet[0];
            where = smsparsedata.valueSet[3];
            //when = smsparsedata.valueSet[2];
            place = smsparsedata.valueSet[4];
            account=smsparsedata.valueSet[1];
            trans_type = smsparsedata.trans_type;
            card_type = smsparsedata.card_type;
            return true;
        }
        else
            return false;
    }


}
