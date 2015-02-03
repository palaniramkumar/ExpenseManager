package com.reader.ramkumar.expensemanager.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jensdriller.libs.undobar.UndoBar;
import com.reader.ramkumar.expensemanager.R;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.TYPES;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Ramkumar on 27/12/14.
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

        //Add a popup menu. This method set OverFlow button to visible
        header.setPopupMenu(R.menu.popup_item, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_add:
                        //Example: add an item
                        CostObject w1 = new CostObject(NotificationCard.this);
                        w1.message = "Food";
                        w1.messagegId = "8400";
                        w1.setObjectId(w1.message);
                        mLinearListAdapter.add(w1);
                        break;
                    case R.id.action_remove:
                        //Example: remove an item
                        mLinearListAdapter.remove(mLinearListAdapter.getItem(0));
                        break;
                }

            }
        });
        header.setTitle("You have few Pending Approvals"); //should use R.string.
        return header;
    }

    @Override
    protected void initCard() {
        //Set the whole card as swipeable
        setSwipeable(false);
        setUseProgressBar(true);
    }


    @Override
    protected List<ListObject> initChildren() {
        db=new DBHelper(getContext());
        final Cursor cursor= db.getmMasterByStatus(TYPES.TRANSACTION_STATUS.PENDING.toString());
        cursor.moveToFirst();
        //Init the list
        List<ListObject> mObjects = new ArrayList<ListObject>();

        while  (cursor.moveToNext())
        {
             /* this may need to tune further for better accurecy */
            final String amount = cursor.getString(1);
            final int RECID = cursor.getInt(0);
            //Add an object to the list
            CostObject c = new CostObject(this);
            c.message = "Rs."+ amount +" spent at "+cursor.getString(8);
            c.messagegId=RECID+"";
            c.setObjectId(c.messagegId); //It can be important to set ad id
            c.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, final int position, ListObject object) {
                    //need to add code for arg parameter for add expense

                    Toast.makeText(getContext(), "Click on " + position, Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(getContext(), Expense_add_window.class);
                    intent.putExtra("RECID", RECID);
                    getContext().startActivity(intent);*/
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                    final CharSequence myList[] = db.getDefaultCategory();

                    dialog.setSingleChoiceItems(myList, -1,  new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {


                        }
                    });
                    dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ListView lw = ((AlertDialog) dialog).getListView();
                            if(lw.getCheckedItemPosition()>=0) {
                               Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                               db.updateMaster(RECID, db.MASTER_COLUMN_CATEGORY, checkedItem.toString());
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
        TextView timestamp = (TextView) convertView.findViewById(R.id.card_notification_time);

        //Retrieve the values from the object
        CostObject costObject = (CostObject) object;
        content.setText(costObject.message);
        timestamp.setText(costObject.messagegId);

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
        public String messagegId;

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
            db.updateMasterStatus(token.describeContents(),"PENDING");
            mLinearListAdapter.add(deletedObject);
            log("onUndo() " + token.describeContents());
        }

        public void log(String text) {
            Toast.makeText(getContext(), "Swipe on " + text, Toast.LENGTH_SHORT).show();

        }

        private void init() {
            //OnClick Listener
           /* setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
            */
            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(final ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                    final String objId=object.getObjectId();
                    deletedObject=object;
                    db.updateMasterStatus(Integer.parseInt(objId),"DELETED");

                    //new TestDialog(getContext()).show();

                    UndoBar undoBar = new UndoBar.Builder((Activity) getContext())//
                            .setMessage("Successfully Deleted ")//
                            .setStyle(UndoBar.Style.LOLLIPOP)//
                            .setUndoColor(getContext().getResources().getColor(R.color.accent))
                            .setUndoToken(new Parcelable() {
                                @Override
                                public int describeContents() {
                                    return Integer.parseInt(objId);
                                }

                                @Override
                                public void writeToParcel(Parcel dest, int flags) {
                                    dest.writeValue(object);
                                }
                            })
                            .create();
                    // final LogView logView=new LogView(getContext());
                    bind(undoBar);
                    undoBar.show();
                }
            });
        }

    }


}
