package com.reader.ramkumar.expensemanager.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jensdriller.libs.undobar.UndoBar;
import com.reader.ramkumar.expensemanager.BuildConfig;
import com.reader.ramkumar.expensemanager.R;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;
import com.reader.ramkumar.expensemanager.util.TYPES;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Ramkumar on 03/02/15.
 * Card display for user categories
 * Getting and Setting up budget and user Categories
 */
public class CategoryCard extends CardWithList {
    DBHelper db;
    public interface Constants {
        String TAG = "adapter.CategoryCard";
    }
    public CategoryCard(Context context) {
        super(context);
        db=new DBHelper(context);
    }

    @Override
    protected CardHeader initCardHeader() {

        return new CustomHeader(getContext());
    }
    public class CustomHeader extends CardHeader {

        public CustomHeader(Context context) {
            super(context, R.layout.card_header_inner);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            int totalBudget = db.getBudget();
            if (view != null) {
                TextView t1 = (TextView) view.findViewById(R.id.text_exmple_card1);
                if (t1 != null)
                    t1.setText("Total Budget "+Common.CURRENCY+totalBudget);

            }
        }
    }

    @Override
    protected void initCard() {
        //Set the whole card as swipeable
        setSwipeable(false);
    }


    @Override
    protected List<ListObject> initChildren() {

        //Init the list
        final List<ListObject> mObjects = new ArrayList<ListObject>();
        //Get User categories and budget
        final Cursor cursor = db.getMyBudgetByCategory();
        //Add an object to the list

        while(cursor.moveToNext()){
            CategoryObject c = new CategoryObject(this);
            int tmp_amount =cursor.getInt(2);
            final int id=cursor.getInt(0);
            String tmp_type = cursor.getString(1);
            c.type = tmp_type; //setter for categoryObject
            c.amount = tmp_amount;
            c.trendIcon = R.drawable.ic_action_expand; //need to implement with Dynamic icons by comparing with last month expense
            c.setObjectId(cursor.getInt(0)+"");
            c.setOnItemClickListener(new OnItemClickListener() {
                 @Override
                 public void onItemClick(final LinearListView parent, View view, final int position, final ListObject object) {
                     AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                     Cursor c = db.getCategory(id);
                     if(c.moveToNext()) {
                         //you should edit this to fit your needs
                         builder.setTitle("Edit Category");

                         final EditText txt_category = new EditText(getContext());
                         txt_category.setHint("Category");//optional
                         txt_category.setText(c.getString(c.getColumnIndex(DBHelper.MASTER_COLUMN_CATEGORY)));
                         final EditText txt_amount = new EditText(getContext());
                         txt_amount.setHint("Amount");//optional
                         txt_amount.setText(c.getString(c.getColumnIndex(DBHelper.MASTER_COLUMN_AMOUNT)));;

                         // use TYPE_CLASS_NUMBER for input only numbers
                         txt_category.setInputType(InputType.TYPE_CLASS_TEXT);
                         txt_amount.setInputType(InputType.TYPE_CLASS_NUMBER);
                         //Setting up dynamic dialog
                         LinearLayout lay = new LinearLayout(getContext());
                         lay.setOrientation(LinearLayout.VERTICAL);
                         lay.addView(txt_category);
                         lay.addView(txt_amount);
                         builder.setView(lay);

                         // Set up the buttons
                         builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int whichButton) {
                                 //get the two inputs
                                 String category = txt_category.getText().toString();
                                 String budget = txt_amount.getText().toString();
                                 db.updateCategory(Integer.parseInt(object.getObjectId()), "category", category);
                                 db.updateCategory(Integer.parseInt(object.getObjectId()), "amount", budget);

                                 //realtime update at table
                                 TextView txt_amount = (TextView) parent.getChildAt(position).findViewById(R.id.table_txt_amount);
                                 txt_amount.setText(budget);
                                 ((CategoryObject) object).amount = Integer.parseInt(budget);// This code is not updating in real time, need a page refresh
                                 parent.refreshDrawableState();
                             }
                         });

                         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int whichButton) {
                                 dialog.cancel();
                             }
                         });
                         builder.show();
                     }

                 }
             });
            mObjects.add(c);
        }

        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView category = (TextView) convertView.findViewById(R.id.txt_category);
        ImageView icon = (ImageView) convertView.findViewById(R.id.status_icon);
        TextView amount = (TextView) convertView.findViewById(R.id.table_txt_amount);
        TextView currency = (TextView) convertView.findViewById(R.id.txt_currency);

        //Retrieve the values from the object
        CategoryObject categoryObject = (CategoryObject) object;
        //icon.setImageResource(categoryObject.trendIcon);
        category.setText(categoryObject.type);
        amount.setText(categoryObject.currencyUnit+categoryObject.amount);
        //currency.setText(categoryObject.currencyUnit);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_table_summary;
    }

    // -------------------------------------------------------------
    // Cost Object
    // -------------------------------------------------------------

    public class CategoryObject extends DefaultListObject implements UndoBar.Listener {

        public String type;
        public int trendIcon;
        public int amount;
        public String currencyUnit = Common.CURRENCY;//"₹"

        public CategoryObject(Card parentCard) {
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
            db.updateCategory(token.describeContents(), "status", TYPES.TRANSACTION_STATUS.APPROVED.toString());
            mLinearListAdapter.add(deletedObject);
            log("onUndo() " + token.describeContents());
        }
        public void log(String text) {
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Swipe on " + text);
            }
        }
        private void init() {
            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(final ListObject object, boolean dismissRight) {
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.TAG, "Swipe on " + object.getObjectId());
                    }
                  
                    final String objId=object.getObjectId();
                    deletedObject=object;
                    db.updateCategory(Integer.parseInt(objId), "status", TYPES.TRANSACTION_STATUS.DELETED.toString());

                    //invoke undobar if user swipe the record (delete call)
                    UndoBar undoBar = new UndoBar.Builder((Activity) getContext())//
                            .setMessage("Successfully Deleted ")
                            .setStyle(UndoBar.Style.LOLLIPOP)//Undo bar style
                            .setUndoColor(getContext().getResources().getColor(R.color.myAccentColor))
                            .setAlignParentBottom(true)
                            .setUndoToken(new Parcelable() {
                                @Override
                                public int describeContents() {
                                    return Integer.parseInt(objId);//this value will be used in onundo() call
                                }

                                @Override
                                public void writeToParcel(Parcel dest, int flags) {
                                    dest.writeValue(object); //currently no impact with this code
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
