package com.reader.freshmanapp.mywallet.adapter;

/**
 * Created by Ramkumar on 28/12/14.
 * getter and setter class for radio list like Gmail
 */
public class ListAdapterRadioModel {
    private String name;
    private boolean selected;

    public ListAdapterRadioModel(String name) {
        this.name = name;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
