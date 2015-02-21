package com.reader.ramkumar.expensemanager.adapter;

/**
 * Created by Ram on 31/12/2014.
 * http://www.vogella.com/tutorials/AndroidListView/article.html
 * Customised expandable adapter from the above URL
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.ramkumar.expensemanager.Expense_add_window;
import com.reader.ramkumar.expensemanager.R;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.TYPES;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private final SparseArray<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;
    String trans_type;
    String sms_id;
    String category;
    int id;
    DBHelper db;

    public MyExpandableListAdapter(Activity act, SparseArray<Group> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
        db = new DBHelper(act.getApplicationContext());
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }


    /*code for displaying content in grouplist*/
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition); //this array of data added in the group from the fragment
        TextView text = null;
        TextView amount = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_details, null);
        }
        /*setting up values into the list*/
        final Cursor cursor = db.getFromMasterByID(Integer.parseInt(children));

        final String amt ;


        text = (TextView) convertView.findViewById(R.id.textView1);
        amount = (TextView) convertView.findViewById(R.id.textView2);

        if(cursor.moveToNext()) { /* this code displays the amount and the category in the list */
            id=  cursor.getInt(0);
            trans_type=cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE));
            sms_id = cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_SMS_ID));
            category=cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_CATEGORY));
            amt = cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_AMOUNT));
            text.setText(category);
            amount.setText(amt);
        }
        final View view = convertView;
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { /*this code always invoked when the user clicked the list item*/
                final Cursor cursor = db.getFromMasterByID(Integer.parseInt(children));

                if(cursor.moveToNext()) {
                    id=  cursor.getInt(0);
                    trans_type=cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE));
                    sms_id = cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_SMS_ID));
                    category=cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_CATEGORY));

                }

                    if(trans_type.equalsIgnoreCase(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString())){
                        showDialogConfirm(view.getContext(),id);
                    }
                    else if(sms_id!=null)
                        showDialogCatogories(view.getContext(),id);

                    else {

                        Intent i = new Intent(view.getContext(), Expense_add_window.class);
                        i.putExtra("RECID", children);
                        ((Activity)view.getContext()).startActivityForResult(i,201); //201 -Create: assume HTTP 201 for create request :). It can be any value
                        //view.getContext().startActivity(i);
                    }


            }
        });
        return convertView;
    }



    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_group, null);
        }
        Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    void showDialogCatogories(final Context context, final int RECID){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);

        //final CharSequence myList[] = db.getDefaultCategory();
        Cursor myList =db.getMyBudgetByCategory();
        dialog.setSingleChoiceItems(myList, -1,"category",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();
                if(lw.getCheckedItemPosition()>=0) {
                    Cursor checkedItem =(Cursor) lw.getAdapter().getItem(lw.getCheckedItemPosition());
                    db.updateMaster(RECID, db.MASTER_COLUMN_CATEGORY, checkedItem.getString(checkedItem.getColumnIndex("category")));
                }
                // TODO Auto-generated method stub

            }

        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });


        // Set dialog title
        dialog.setTitle("Choose the Category");
        dialog.show();
    }
    void showDialogConfirm(final Context context, final int RECID){
        new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Delete entry")
                .setMessage("Remove from Cash Vault ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.updateMasterStatus(RECID, TYPES.TRANSACTION_STATUS.PENDING.toString());

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
