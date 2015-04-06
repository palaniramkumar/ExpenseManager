package com.reader.freshmanapp.mywallet.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.reader.freshmanapp.mywallet.db.DBHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;

/**
 * Created by Ramkumar on 06/04/15.
 */
public class DbCSVBackup {
    Context mContext;
    public  DbCSVBackup(Context context){
        mContext = context;
    }
    public boolean export(){
        int i = 0;
        Boolean returnCode = true;
        String csvHeader = "";
        String csvValues = "";
        DBHelper db= new DBHelper(mContext);
        Cursor rs = db.getFromMaster();
        for (i = 0; i < rs.getColumnCount(); i++) {
            if (csvHeader.length() > 0) {
                csvHeader += ",";
            }
            csvHeader += "\"" + rs.getColumnName(i) + "\"";
        }
        csvHeader += "\n";
        String outFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/export.csv";
        try {
            File outFile = new File(outFileName);
            FileWriter fileWriter = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(csvHeader);
            while (rs.moveToNext()) {
                for (i = 0; i < rs.getColumnCount(); i++) {
                    if (i==rs.getColumnCount()-1)
                        out.write(rs.getString(i));
                    else
                        out.write(rs.getString(i)+",");

                }

                out.write("\n");
            }
            out.close();
            rs.close();
        }
        catch (IOException e) {
            returnCode = false;
            Log.d("CSV Export", "IOException: " + e.getMessage());
        }
        db.close();
        Log.d("CSV Export", outFileName);
        return returnCode;
    }

}

