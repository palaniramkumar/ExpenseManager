package com.reader.ramkumar.expensemanager.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.reader.ramkumar.expensemanager.R;

import java.util.List;
import java.util.Random;

/**
 * Created by Ramkumar on 28/12/14.
 */
public class ListAdapterForRadioButton extends ArrayAdapter<ListAdapterRadioModel> {
    private final List<ListAdapterRadioModel> list;
    private final Activity context;

    public ListAdapterForRadioButton(Activity context, List<ListAdapterRadioModel> list) {
        super(context, R.layout.list_radio_items, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_radio_items, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.color = (TextView) view.findViewById(R.id.item_color);
            viewHolder.radioButton = (RadioButton) view.findViewById(R.id.radio);
            viewHolder.radioButton
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            ListAdapterRadioModel element = (ListAdapterRadioModel) viewHolder.radioButton
                                    .getTag();
                            element.setSelected(buttonView.isChecked());

                        }
                    });
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            viewHolder.color.setBackgroundColor(color);
            viewHolder.color.setText(list.get(position).getName().charAt(0)+"");
            view.setTag(viewHolder);
            viewHolder.radioButton.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).radioButton.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getName());
        holder.radioButton.setChecked(list.get(position).isSelected());
        return view;
    }

    static class ViewHolder {
        protected TextView text;
        protected TextView color;
        protected RadioButton radioButton;
    }
}
