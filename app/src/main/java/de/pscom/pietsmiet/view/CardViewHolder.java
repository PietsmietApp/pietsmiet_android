package de.pscom.pietsmiet.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.pscom.pietsmiet.R;


public class CardViewHolder extends RecyclerView.ViewHolder {
    public final CardView cv;
    public final TextView title;
    public final TextView text;
    public final TextView time;
    public final TextView description;
    public final ImageView wideImage;
    public final ImageView thumbnail;
    public final RelativeLayout descriptionContainer;
    public final Button btnExpand;

    public CardViewHolder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cv);
        title = (TextView) itemView.findViewById(R.id.tvTitle);
        text = (TextView) itemView.findViewById(R.id.tvText);
        description = (TextView) itemView.findViewById(R.id.tvDescription);
        time = (TextView) itemView.findViewById(R.id.tvDateTime);
        wideImage = (ImageView) itemView.findViewById(R.id.ivWideImage);
        thumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
        btnExpand = (Button) itemView.findViewById(R.id.btnExpand);
        descriptionContainer = (RelativeLayout) itemView.findViewById(R.id.rlExpandableContainer);
    }
}
