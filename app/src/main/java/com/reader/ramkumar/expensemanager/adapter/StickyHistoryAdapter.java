package com.reader.ramkumar.expensemanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.reader.ramkumar.expensemanager.*;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Ram on 13/02/2015
 * URL: https://github.com/emilsjolander/StickyListHeaders/.
 */
public class StickyHistoryAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private String[] entry_day;
    private String[] category;
    private String[] amount;
    private String[] place;
    private String[] id;
    private String month;
    private LayoutInflater inflater;
    DBHelper db;
    public StickyHistoryAdapter(Context context,DBHelper db) {
        inflater = LayoutInflater.from(context);
        this.db =db;
        Cursor cur = db.getTransactionHistory();
        entry_day = new String[cur.getCount()];
        category = new String[cur.getCount()];
        amount = new String[cur.getCount()];
        place = new String[cur.getCount()];
        id = new String[cur.getCount()];
        int i=0;
        while(cur.moveToNext()){

            entry_day[i] = cur.getString(1);
            month = cur.getString(0);
            amount[i] = cur.getString(3);
            category[i] = cur.getString(2);
            place[i] = cur.getString(5);
            id[i] = cur.getString(4);
            i++;
        }
        System.out.println("Total Entry: "+i);
        //entry_day = context.getResources().getStringArray(R.array.entry_day);
    }

    @Override
    public int getCount() {
        return entry_day.length;
    }

    @Override
    public Object getItem(int position) {
        return entry_day[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.sticky_list_item_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text); //list item value
            holder.txt_amount = (TextView) convertView.findViewById(R.id.txt_amount);
            holder.txt_desc = (TextView) convertView.findViewById(R.id.txt_desc);
            holder.txt_id = (TextView) convertView.findViewById(R.id.txt_id);
            holder.txt_category = (TextView) convertView.findViewById(R.id.txt_category);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(entry_day[position]);
        holder.txt_amount.setText(Common.CURRENCY+" "+ amount[position]);
        holder.txt_desc.setText(place[position]);
        holder.txt_category.setText(category[position]);
        holder.txt_id.setText(id[position]);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.sticky_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = month +", " + entry_day[position]; //code for showing header
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return Integer.parseInt(entry_day[position]); //code for grouping values
    }

    class HeaderViewHolder {
        TextView text;

    }

    class ViewHolder {
        TextView text;
        TextView txt_amount;
        TextView txt_desc;
        TextView txt_id;
        TextView txt_category;
    }

}