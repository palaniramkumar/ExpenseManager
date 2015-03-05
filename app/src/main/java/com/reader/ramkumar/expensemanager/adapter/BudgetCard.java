package com.reader.ramkumar.expensemanager.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reader.ramkumar.expensemanager.BuildConfig;
import com.reader.ramkumar.expensemanager.R;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Ramkumar on 27/12/14.
 * Expense against budget in at home screen
 */

public class BudgetCard extends CardWithList {
    DBHelper db;
    public interface Constants {
        String TAG = "app:BudgetCard";
    }
    public BudgetCard(Context context, DBHelper db) {
        super(context);
        this.db=db;
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

            int remainingAmount = (int)(db.getBudget() - db.getMyTotalExpense());
            if (view != null) {
                TextView t1 = (TextView) view.findViewById(R.id.text_exmple_card1);
                if (t1 != null)
                    t1.setText("You have remaining "+remainingAmount+" Left");

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
        List<ListObject> mObjects = new ArrayList<ListObject>();

        Cursor cursor = db.getBudgetSummary();
        //Add an object to the list

        while(cursor.moveToNext()){
            int pending_amt= cursor.getInt(1)-cursor.getInt(2);
            if(pending_amt==0 &&  cursor.getInt(1) ==0) continue; //code for skiping content disp when the budget is 0 and no expense is tracked so far in that department
            CostObject c = new CostObject(this);
            c.type = cursor.getString(0);
            c.amount = cursor.getInt(1)-cursor.getInt(2);
            c.trendIcon = R.drawable.ic_action_expand;
            c.setObjectId(c.type); //It can be important to set tyoe id, In future we shall show all the curresponding ID expense in history fragment
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
        CostObject costObject = (CostObject) object;
        icon.setImageResource(costObject.trendIcon);
        category.setText(costObject.type);
        amount.setText(costObject.amount+"");
        currency.setText(costObject.currencyUnit);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_table_summary;
    }

    // -------------------------------------------------------------
    // Cost Object
    // -------------------------------------------------------------

    public class CostObject extends DefaultListObject {

        public String type;
        public int trendIcon;
        public int amount;
        public String currencyUnit = Common.CURRENCY;//"₹"

        public CostObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.TAG, "Clicked on " + getObjectId());
                    }

                }
            });

        }

    }


}
