package com.reader.ramkumar.expensemanager.util;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.reader.ramkumar.expensemanager.R;

import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by Ramkumar on 21/03/15.
 */
public class UndoBar implements com.jensdriller.libs.undobar.UndoBar.Listener{
    com.jensdriller.libs.undobar.UndoBar undoBar;
    public UndoBar(Context context){
        undoBar = new com.jensdriller.libs.undobar.UndoBar.Builder((Activity) context)
                .setStyle(com.jensdriller.libs.undobar.UndoBar.Style.LOLLIPOP)
                .setUndoColor(context.getResources().getColor(R.color.myAccentColor))
                .setAlignParentBottom(true)
                .create();
        undoBar.setListener(this);
    }
    @Override
    public void onHide() {
        //
    }

    @Override
    public void onUndo(Parcelable token) {
      //
    }
    public void show(String msg){
        undoBar.setMessage(msg);
        undoBar.setUseEnglishLocale(true);
        undoBar.show(true);
    }

}
