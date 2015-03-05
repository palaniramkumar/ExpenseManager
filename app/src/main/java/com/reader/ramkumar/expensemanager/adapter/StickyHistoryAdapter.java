package com.reader.ramkumar.expensemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.reader.ramkumar.expensemanager.*;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;
import com.reader.ramkumar.expensemanager.util.TYPES;

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
    Context mContext;
    public interface Constants {
        String TAG = "app:StickyAdapter";
    }
    public StickyHistoryAdapter(Context context,DBHelper db,String filter) {
        inflater = LayoutInflater.from(context);
        this.db =db;
        Cursor cur = db.getTransactionHistory(filter);
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
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG,"Total Entry: "+i);
        }
        mContext = context;
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.sticky_list_item_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text); //list item value
            holder.txt_amount = (TextView) convertView.findViewById(R.id.txt_amount);
            holder.txt_desc = (TextView) convertView.findViewById(R.id.txt_desc);
            holder.txt_id = (TextView) convertView.findViewById(R.id.txt_id);
            holder.txt_category = (TextView) convertView.findViewById(R.id.txt_category);
            holder.popupMenu = (ImageView)convertView.findViewById(R.id.more);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(entry_day[position]);
        holder.txt_amount.setText(Common.CURRENCY+" "+ amount[position]);
        holder.txt_desc.setText(place[position]);
        holder.txt_category.setText(category[position]);
        holder.txt_id.setText(id[position]);
        final View view = convertView;
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /** Instantiating PopupMenu class */
                Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(wrapper, v);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.history_popup, popup.getMenu());

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().toString().equalsIgnoreCase("Delete")){
                            db.updateMaster(Integer.parseInt(holder.txt_id.getText().toString()),db.MASTER_COLUMN_STATUS, TYPES.TRANSACTION_STATUS.DELETED+"");
                            view.findViewById(R.id.itm_layout).setVisibility(View.GONE);

                        }
                        return true;
                    }
                });

                /** Showing the popup menu */
                popup.show();


            }
        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.sticky_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            holder.amount = (TextView) convertView.findViewById(R.id.sum_amount);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = month +", " + entry_day[position]; //code for showing header
        String headerAmount = Common.CURRENCY+" "+db.getExpensebyDay(entry_day[position]);
        holder.text.setText(headerText);
        holder.amount.setText(headerAmount);
        
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return Integer.parseInt(entry_day[position]); //code for grouping values
    }

    class HeaderViewHolder {
        TextView text;
        TextView amount;
    }

    class ViewHolder {
        TextView text;
        TextView txt_amount;
        TextView txt_desc;
        TextView txt_id;
        TextView txt_category;
        ImageView popupMenu;
    }

}