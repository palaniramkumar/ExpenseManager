package com.reader.ramkumar.expensemanager.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ram on 31/12/2014.
 * convert expense history in to groups
 */
public class Group {
    public final String string;
    public final List<String[]> children = new ArrayList<String[]>() ;

    public Group(String string) {
        this.string = string;
    }
}
