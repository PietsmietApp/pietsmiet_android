package de.pscom.pietsmiet.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.pscom.pietsmiet.R;


public class CardViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cv) CardView cv;
    @BindView(R.id.tvTitle) TextView title;
    @BindView(R.id.tvText) TextView text;
    @BindView(R.id.tvDateTime) TextView time;
    @BindView(R.id.ivTimeIcon) ImageView timeClockImage;
    @BindView(R.id.tvExpandedDescription) TextView expandedDescription;
    @BindView(R.id.ivWideImage) ImageView wideImage;
    @BindView(R.id.rlHeadlineContainer) RelativeLayout headlineContainer;
    @BindView(R.id.rlDescriptionContainer) RelativeLayout descriptionContainer;
    @BindView(R.id.rlExpandableContainer) RelativeLayout expandableContainer;
    @BindView(R.id.rlBottomContainer) RelativeLayout bottomContainer;
    @BindView(R.id.rlTimeContainer) RelativeLayout timeContainer;
    @BindView(R.id.tvLine) TextView line;
    @BindView(R.id.tvUsername) TextView username;
    @BindView(R.id.btnExpand) Button btnExpand;
    @BindView(R.id.ivPostTypeLogo) ImageView postTypeLogo;

    public CardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
