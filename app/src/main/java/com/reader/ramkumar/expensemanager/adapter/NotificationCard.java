package com.reader.ramkumar.expensemanager.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jensdriller.libs.undobar.UndoBar;
import com.reader.ramkumar.expensemanager.R;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;
import com.reader.ramkumar.expensemanager.util.TYPES;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Ramkumar on 27/12/14.
 * Bank card transaction in notification panel
 */
public class NotificationCard extends CardWithList {
    DBHelper db;
    public NotificationCard(Context context) {
        super(context);
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.card_table_header);

        header.setTitle("You have few Pending Approvals"); //should use R.string.
        return header;
    }

    @Override
    protected void initCard() {
        //Set the whole card as swipeable
        setSwipeable(false);
        setUseProgressBar(true);
    }

    //this code is duplicated in myexpandablelist adapter

    void showDialogCatogories(final int RECID,final int position){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);

        //final CharSequence myList[] = db.getDefaultCategory();
        Cursor myList =db.getMyBudgetByCategory(); //to showup all the categories in the approval dialog
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
                    db.updateMasterStatus(RECID, TYPES.TRANSACTION_STATUS.APPROVED.toString());
                    mLinearListAdapter.remove(mLinearListAdapter.getItem(position));
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
    void showDialogConfirm(final int RECID,final int position){
        new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Delete entry")
                .setMessage("Can I add this is Cash Vault ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.updateMasterStatus(RECID, TYPES.TRANSACTION_STATUS.APPROVED.toString());
                        mLinearListAdapter.remove(mLinearListAdapter.getItem(position));
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


    @Override
    protected List<ListObject> initChildren() {
        db=new DBHelper(getContext());
        final Cursor cursor= db.getFromMaster(db.MASTER_COLUMN_CATEGORY,db.UNCATEGORIZED); //last 30 days
        cursor.moveToFirst();
        //Init the list
        List<ListObject> mObjects = new ArrayList<ListObject>();

        while  (cursor.moveToNext())
        {
             /* this may need to tune further for better accurecy */
            final String amount = cursor.getString(1);
            final int RECID = cursor.getInt(0);
            final String trans_type=cursor.getString(cursor.getColumnIndex(DBHelper.MASTER_COLUMN_TRANS_TYPE));
            //Add an object to the list
            CostObject c = new CostObject(this);
            c.message = cursor.getString(8);
            c.amount = Common.CURRENCY+". " + amount;
            c.trans_src = cursor.getString(cursor.getColumnIndex(db.MASTER_COLUMN_TRANS_SOURCE));
            c.messagegId=cursor.getInt(0)+"";//RECID+"";
            try {
                c.ts = db.getJavaDate(cursor.getString(cursor.getColumnIndex(db.MASTER_COLUMN_TRANSACTION_TIME)));
                System.out.println(c.ts);
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            c.setObjectId(c.messagegId); //It can be important to set ad id
            c.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, final int position, ListObject object) {
                    if(trans_type.equalsIgnoreCase(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString())){
                        showDialogConfirm(RECID,position);
                    }
                    else
                        showDialogCatogories(RECID,position);


                }
            });
            mObjects.add(c);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mObjects;
    }

    /* XML field & values Mapping */
    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView content = (TextView) convertView.findViewById(R.id.card_notification_content);
        TextView amount = (TextView) convertView.findViewById(R.id.card_notification_amount);
        TextView timestamp = (TextView) convertView.findViewById(R.id.card_notification_time);
        TextView tran_src = (TextView) convertView.findViewById(R.id.card_notification_tran_src);

        //Retrieve the values from the object
        CostObject costObject = (CostObject) object;
        content.setText(costObject.message.toUpperCase());
        timestamp.setText(costObject.ts);
        amount.setText(costObject.amount);
        tran_src.setText(costObject.trans_src);
        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_table_notification;
    }


    // -------------------------------------------------------------
    // Cost Object
    // -------------------------------------------------------------

    public class CostObject extends DefaultListObject implements UndoBar.Listener {

        public String message;
        public String amount;
        public String trans_src;
        public String messagegId;
        public String ts;

        public CostObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public void bind(UndoBar undoBar) {
            undoBar.setListener(this);
        }

        @Override
        public void onHide() {
            log("onHide()");
        }

        ListObject deletedObject;
        @Override
        public void onUndo(Parcelable token) {
            db.updateMasterStatus(token.describeContents(),TYPES.TRANSACTION_STATUS.APPROVED.toString());
            mLinearListAdapter.add(deletedObject);
            log("onUndo() " + token.describeContents());
        }

        public void log(String text) {
            Toast.makeText(getContext(), "Swipe on " + text, Toast.LENGTH_SHORT).show();

        }

        private void init() {
            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(final ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                    final String objId=object.getObjectId();
                    deletedObject=object;
                    db.updateMasterStatus(Integer.parseInt(objId),TYPES.TRANSACTION_STATUS.DELETED.toString());

                    //new TestDialog(getContext()).show();

                    UndoBar undoBar = new UndoBar.Builder((Activity) getContext())//
                            .setMessage("Successfully Deleted ")//
                            .setStyle(UndoBar.Style.LOLLIPOP)//
                            .setUndoColor(getContext().getResources().getColor(R.color.accent))
                            .setAlignParentBottom(true)
                            .setUndoToken(new Parcelable() {
                                @Override
                                public int describeContents() {
                                    return Integer.parseInt(objId);//this value will be used in onundo() call
                                }

                                @Override
                                public void writeToParcel(Parcel dest, int flags) {
                                    dest.writeValue(object); //code has no imact
                                }
                            })
                            .create();
                    bind(undoBar);
                    undoBar.show();
                }
            });
        }

    }


}
