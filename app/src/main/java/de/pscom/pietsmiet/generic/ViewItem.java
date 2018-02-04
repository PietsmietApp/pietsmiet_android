package de.pscom.pietsmiet.generic;

import android.support.annotation.NonNull;

import java.util.Date;

public class ViewItem implements Comparable<ViewItem>{
    public static final int TYPE_POST = 0;
    public static final int TYPE_DATE_TAG = 1;

    protected Date datetime;
    private int type;

    @NonNull
    public Date getDate() {
        return datetime;
    }

    public int getType() { return type; }

    public ViewItem(int type, @NonNull Date date) {
        this.type = type;
        this.datetime = date;
    }

    @Override
    public int compareTo(@NonNull ViewItem o) {
        return o.getDate().compareTo(this.getDate());
    }
}
