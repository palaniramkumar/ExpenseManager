package com.reader.freshmanapp.mywallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import com.reader.freshmanapp.mywallet.BuildConfig;
import com.reader.freshmanapp.mywallet.R;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.Common;
import com.reader.freshmanapp.mywallet.util.TYPES;

import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Ram on 13/02/2015
 * URL: https://github.com/emilsjolander/StickyListHeaders/.
 */
public class StickyHistoryAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    DBHelper db;
    Context mContext;
    private String[] entry_day;
    private String[] category;
    private String[] amount;
    private String[] place;
    private String[] id;
    private String[] gps;
    private String month;
    private String category_filter;
    private LayoutInflater inflater;

    public StickyHistoryAdapter(Context context, DBHelper db, String filter) {
        inflater = LayoutInflater.from(context);
        this.db = db;
        Cursor cur = db.getTransactionHistory(filter);
        entry_day = new String[cur.getCount()];
        category = new String[cur.getCount()];
        amount = new String[cur.getCount()];
        place = new String[cur.getCount()];
        id = new String[cur.getCount()];
        gps = new String[cur.getCount()];
        category_filter = filter;
        int i = 0;
        while (cur.moveToNext()) {

            entry_day[i] = cur.getString(1);
            month = cur.getString(0);
            amount[i] = cur.getString(3);
            category[i] = cur.getString(2);
            place[i] = cur.getString(5);
            id[i] = cur.getString(4);
            gps[i] = cur.getString(cur.getColumnIndex(db.MASTER_COLUMN_GEO_TAG));
            i++;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Total Entry: " + i);
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
            holder.popupMenu = (ImageView) convertView.findViewById(R.id.more);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(entry_day[position]);
        holder.txt_amount.setText(Common.CURRENCY + " " + amount[position]);
        holder.txt_desc.setText(place[position]);
        holder.txt_category.setText(category[position]);
        holder.txt_id.setText(id[position]);
        holder.gps = gps[position];

        final View view = convertView;
        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /** Instantiating PopupMenu class */
                Context wrapper = new ContextThemeWrapper(mContext, R.style.popupMenuStyle);
                PopupMenu popup = new PopupMenu(wrapper, v);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.history_popup, popup.getMenu());
                if (holder.gps != null)
                    popup.getMenu().add("Locate");

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
                            db.updateMaster(Integer.parseInt(holder.txt_id.getText().toString()), db.MASTER_COLUMN_STATUS, TYPES.TRANSACTION_STATUS.DELETED + "");
                            view.findViewById(R.id.itm_layout).setVisibility(View.GONE);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Locate")) {
                            String gps_loc = holder.gps;
                            String latitude = gps_loc.split(",")[0];
                            String longitude = gps_loc.split(",")[1];
                            String uri = String.format(Locale.ENGLISH, "geo:%s,%s", latitude, longitude);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            mContext.startActivity(intent);
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
        String headerText = month + ", " + entry_day[position]; //code for showing header
        String headerAmount = Common.CURRENCY + " " + db.getExpensebyDay(entry_day[position], category_filter);
        holder.text.setText(headerText);
        holder.amount.setText(headerAmount);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return Integer.parseInt(entry_day[position]); //code for grouping values
    }

    public interface Constants {
        String TAG = "app:StickyAdapter";
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
        String gps;
        ImageView popupMenu;
    }

}