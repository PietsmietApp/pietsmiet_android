package de.pscom.pietsmiet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.pscom.pietsmiet.R;

public class DateTagCardViewHolder extends RecyclerView.ViewHolder {
    public final TextView tvDate;

    public DateTagCardViewHolder(View itemView) {
        super(itemView);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
    }
}
