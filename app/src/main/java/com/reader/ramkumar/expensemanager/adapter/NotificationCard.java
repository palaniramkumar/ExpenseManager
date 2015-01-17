package com.reader.ramkumar.expensemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jensdriller.libs.undobar.UndoBar;
import com.reader.ramkumar.SMSparser.SMS;
import com.reader.ramkumar.expensemanager.R;

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
    public NotificationCard(Context context) {
        super(context);
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        //Add a popup menu. This method set OverFlow button to visible
        header.setPopupMenu(R.menu.popup_item, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_add:
                        //Example: add an item
                        CostObject w1 = new CostObject(NotificationCard.this);
                        w1.message = "Food";
                        w1.info = "8400";
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
        header.setTitle("You have 12 Pending Approvals"); //should use R.string.
        return header;
    }

    @Override
    protected void initCard() {

        //Set the whole card as swipeable
        setSwipeable(true);
        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                Toast.makeText(getContext(), "Swipe on " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected List<ListObject> initChildren() {

        ArrayList sms = new ArrayList();

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor =getContext().getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);

        cursor.moveToFirst();
        //Init the list
        List<ListObject> mObjects = new ArrayList<ListObject>();

        while  (cursor.moveToNext())
        {
            String address = cursor.getString(1);
            String body = cursor.getString(3);

            /* custom code*/
            SMS s= new SMS();
            s.address=address;
            s.text=body;
            s.id=cursor.getString(0);
            s.when=cursor.getString(2);




             /* this may need to tune further for better accurecy */
            if(s.findSMS() && s.amount!=null) {
                //Add an object to the list
                CostObject w1 = new CostObject(this);
                w1.message = "Rs."+s.amount +" spent on "+s.where;
                w1.setObjectId(w1.info); //It can be important to set ad id
                mObjects.add(w1);


            }

        }

        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/
        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView city = (TextView) convertView.findViewById(R.id.carddemo_weather_city);
        TextView temperature = (TextView) convertView.findViewById(R.id.carddemo_weather_temperature);

        //Retrieve the values from the object
        CostObject costObject = (CostObject) object;
        city.setText(costObject.message);
        temperature.setText(costObject.info);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_table_summary;
    }


    // -------------------------------------------------------------
    // Weather Object
    // -------------------------------------------------------------

    public class CostObject extends DefaultListObject implements UndoBar.Listener {

        public String message;
        public String info;

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

        @Override
        public void onUndo(Parcelable token) {
            log("onUndo() " + token.describeContents());
        }

        public void log(String text) {
            Toast.makeText(getContext(), "Swipe on " + text, Toast.LENGTH_SHORT).show();
       /* mTxtLog.append("\n");
        mTxtLog.append("#" + ++mLogCount + " ");
        mTxtLog.append(text);*/
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                    //new TestDialog(getContext()).show();
                    UndoBar undoBar = new UndoBar.Builder((Activity) getContext())//
                            .setMessage("Undo Me!")//
                            .setStyle(UndoBar.Style.LOLLIPOP)//
                            .setUndoColor(getContext().getResources().getColor(R.color.accent))
                            .setUndoToken(new Parcelable() {
                                @Override
                                public int describeContents() {
                                    return 12;
                                }

                                @Override
                                public void writeToParcel(Parcel dest, int flags) {

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
