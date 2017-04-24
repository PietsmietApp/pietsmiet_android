package de.pscom.pietsmiet.generic;

import android.support.annotation.NonNull;

import java.util.Date;

public class DateTag extends ViewItem {

    public DateTag(Date date) {
        super(ViewItem.TYPE_DATE_TAG);
        this.datetime = date;
    }

}
