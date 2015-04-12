package com.reader.freshmanapp.SMSparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ramkumar on 11/04/15.
 */
public class Master {
    public class SMSParserData {
        public String valueSet[];
        public String trans_type;
        public String trans_src;
    }

    /*code for parsing sms. this is generic can be moved to the common class*/
    public SMSParserData parseSMS(String [][] template, int [][] templateMap,String sms) {
        SMSParserData parserValue = new SMSParserData();
        for (int i = 0; i < template.length; i++) {
            Pattern pattern = Pattern.compile(template[i][0]); //fetch the only sms
            Matcher matcher = pattern.matcher(sms);
            parserValue.valueSet = new String[6]; // six is the len of template item count
            if (matcher.find()) {
                for (int j = 0; j < templateMap[i].length; j++) {
                    System.out.println(matcher.group(j + 1));
                    parserValue.valueSet[templateMap[i][j]] = matcher.group(j + 1); //iterate through templateMap.matcher group values always starts with 1.
                }
            }
            if (parserValue.valueSet[0] != null) { //if values parsed, set the trans source and type from the curresponing template
                parserValue.trans_src = template[i][2];
                parserValue.trans_type = template[i][1];
                break;
            }
        }
        return parserValue;
    }

}
