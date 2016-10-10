package de.pscom.pietsmiet.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.PsLog;

import static android.view.View.GONE;
import static de.pscom.pietsmiet.adapters.CardItem.CardItemType;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {

    public static final int LAYOUT_VIDEO = 0;
    public static final int LAYOUT_SOCIAL = 1;

    private List<CardItem> items;
    private Context context;

    public CardViewAdapter(List<CardItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView description;
        TextView timedate;

        CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            timedate = (TextView) itemView.findViewById(R.id.tvDateTime);
        }
    }

    public static class VideoCardViewHolder extends CardViewAdapter.CardViewHolder {
        ImageView preview;
        RelativeLayout descriptionContainer;
        ImageView durationIcon;
        Button btnExpand;

        VideoCardViewHolder(View itemView) {
            super(itemView);
            preview = (ImageView) itemView.findViewById(R.id.ivPreview);
            durationIcon = (ImageView) itemView.findViewById(R.id.ivDuration);
            btnExpand = (Button) itemView.findViewById(R.id.btnExpand);
            descriptionContainer = (RelativeLayout) itemView.findViewById(R.id.rlDescriptionContainer);
        }

    }

    @Override
    public CardViewAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == LAYOUT_VIDEO) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_videos, parent, false);
            return new VideoCardViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_social_media, parent, false);
            return new CardViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(CardViewAdapter.CardViewHolder holder, int position) {
        CardItem currentItem = items.get(position);

        if (holder.getItemViewType() == LAYOUT_VIDEO) {
            VideoCardViewHolder videoHolder = (VideoCardViewHolder) holder;

            Drawable preview = ((VideoCardItem) currentItem).getPreview();
            if (preview != null) {
                videoHolder.preview.setImageDrawable(((VideoCardItem) currentItem).getPreview());
            } else {
                videoHolder.preview.setVisibility(GONE); //Todo add placeholder thumbnail instead of hiding
            }

            videoHolder.durationIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_watch_later_black_24dp));

            videoHolder.btnExpand.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
            videoHolder.btnExpand.setOnClickListener(view -> {
                if (videoHolder.descriptionContainer.getVisibility() == GONE) {
                    videoHolder.descriptionContainer.setVisibility(View.VISIBLE);
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_less_black_24dp));
                } else {
                    videoHolder.descriptionContainer.setVisibility(GONE);
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
                }
            });
        }
        if (currentItem.getCardItemType() == CardItemType.TYPE_UPLOAD_PLAN) {
            holder.timedate.setVisibility(GONE);
        } else if (currentItem.getDatetime().isEmpty()){
            PsLog.v("No Date specified");
            holder.timedate.setVisibility(GONE);
        }

        holder.title.setText(currentItem.getTitle());
        holder.description.setText(currentItem.getDescription());
        holder.timedate.setText(currentItem.getDatetime());
        holder.cv.setCardBackgroundColor(currentItem.getBackgroundColor());
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isVideoView()) return LAYOUT_VIDEO;
        else return LAYOUT_SOCIAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}