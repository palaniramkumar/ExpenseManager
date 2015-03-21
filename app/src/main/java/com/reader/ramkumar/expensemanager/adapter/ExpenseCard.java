package com.reader.ramkumar.expensemanager.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.ramkumar.expensemanager.BuildConfig;
import com.reader.ramkumar.expensemanager.Expense_add_window;
import com.reader.ramkumar.expensemanager.FragmentHistory;
import com.reader.ramkumar.expensemanager.R;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Ramkumar on 27/12/14.
 * Expense at home screen
 */

public class ExpenseCard extends CardWithList {
    DBHelper db;
    Context mContext;
    public interface Constants {
        String TAG = "app:ExpenseCard";
    }
    public ExpenseCard(Context context,DBHelper db) {
        super(context);
        mContext=context;
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

            float amount =  db.getMyTotalExpense();
            if (view != null) {
                TextView t1 = (TextView) view.findViewById(R.id.text_exmple_card1);
                if (t1 != null) {
                    if(amount!=0)
                        t1.setText("The Expense for this month  " + Common.CURRENCY + " " + amount);
                    else
                        t1.setText("No Transactions for this month");
                }

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

        Cursor cursor = db.getMyExpenseByCategory();
        //Add an object to the list

        while(cursor.moveToNext()){
            CostObject c = new CostObject(this);
            c.type = cursor.getString(0);
            c.amount = cursor.getFloat(1);
            c.trendIcon = R.drawable.ic_action_expand;
            c.setObjectId(c.type); //It can be important to set tyoe id, In future we shall show all the curresponding ID expense in history fragment
            /*** current -1 month**/
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -1);

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date);
            cal1.add(Calendar.MONTH, -1);
            cal1.set(Calendar.DAY_OF_MONTH, 1);

            //System.out.println(dateFormat.format(date)); //2013/10/15 16:16:39
            cal.get(Calendar.DATE);
            int prev_month_amt = db.getMyExpenseByCategory(c.type, dateFormat.format(cal1.getTime()), dateFormat.format(cal.getTime()));
            c.progress = prev_month_amt-c.amount>0 ? "▲" :"▼" ;
           // c.progress+=Math.abs(prev_month_amt-c.amount);
            mObjects.add(c);
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
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
        //icon.setImageResource(costObject.trendIcon); //future impl for image icon for better representation
        category.setText(costObject.type);
        amount.setText(costObject.currencyUnit + " " + costObject.amount);

        currency.setText(costObject.progress);

        if(costObject.progress .contains("▼"))
            currency.setTextColor(Color.RED);
        else
            currency.setTextColor(getContext().getResources().getColor(R.color.material_deep_teal_500));

        //currency.setText(costObject.currencyUnit); //can extend later like minified the size etc

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
        public float amount;
        public String currencyUnit = Common.CURRENCY;//"₹"
        public String progress;

        public CostObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    /*Intent i = new Intent(view.getContext(), FragmentHistory.class);
                    i.putExtra("CATEGORY", getObjectId());
                    ((Activity)mContext).startActivity(i);*/
                    FragmentHistory secFrag = new FragmentHistory(getObjectId());
                    FragmentTransaction fragTransaction = ((Activity)mContext).getFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.frame_container,secFrag );
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();
                    
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.TAG, "Clicked on " + getObjectId());
                    }
                }
            });

        }

    }


}
