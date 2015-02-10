package com.reader.ramkumar.SMSparser;


/**
 * Created by Ram on 04/01/2015.
 * SMS value setter from the bank object
 */
public class SMS {
    public String address;
    public String text;
    public String trans_src;
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
            /*0-Amount,1-Account,2-Time,3-Where,4-Place*/
            amount=smsparsedata.valueSet[0];
            where = smsparsedata.valueSet[3];
            //when = smsparsedata.valueSet[2];
            place = smsparsedata.valueSet[4];
            account=smsparsedata.valueSet[1];
            trans_type = smsparsedata.trans_type;
            trans_src = smsparsedata.trans_src;
            return true;
        }
        else
            return false;
    }


}
