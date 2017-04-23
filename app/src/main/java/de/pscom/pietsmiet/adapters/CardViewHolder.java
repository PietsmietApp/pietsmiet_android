package de.pscom.pietsmiet.adapters;

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
    public final ImageView timeClockImage;
    public final TextView expandedDescription;
    public final ImageView wideImage;
    public final RelativeLayout headlineContainer;
    public final RelativeLayout descriptionContainer;
    public final RelativeLayout expandableContainer;
    public final RelativeLayout bottomContainer;
    public final RelativeLayout timeContainer;
    public final TextView line;
    public final TextView username;
    public final Button btnExpand;
    public final ImageView postTypeLogo;

    public CardViewHolder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cv);
        title = (TextView) itemView.findViewById(R.id.tvTitle);
        text = (TextView) itemView.findViewById(R.id.tvText);
        expandedDescription = (TextView) itemView.findViewById(R.id.tvExpandedDescription);
        time = (TextView) itemView.findViewById(R.id.tvDateTime);
        wideImage = (ImageView) itemView.findViewById(R.id.ivWideImage);
        btnExpand = (Button) itemView.findViewById(R.id.btnExpand);
        expandableContainer = (RelativeLayout) itemView.findViewById(R.id.rlExpandableContainer);
        descriptionContainer = (RelativeLayout) itemView.findViewById(R.id.rlDescriptionContainer);
        headlineContainer = (RelativeLayout) itemView.findViewById(R.id.rlHeadlineContainer);
        line = (TextView) itemView.findViewById(R.id.tvLine);
        username = (TextView) itemView.findViewById(R.id.tvUsername);
        postTypeLogo = (ImageView) itemView.findViewById(R.id.ivPostTypeLogo);
        bottomContainer = (RelativeLayout) itemView.findViewById(R.id.rlBottomContainer);
        timeClockImage = (ImageView) itemView.findViewById(R.id.ivTimeIcon);
        timeContainer = (RelativeLayout) itemView.findViewById(R.id.rlTimeContainer);
    }
}
