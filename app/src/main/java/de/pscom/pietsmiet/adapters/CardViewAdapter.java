package de.pscom.pietsmiet.adapters;

import android.content.Context;
import android.graphics.Color;
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

import static de.pscom.pietsmiet.adapters.CardItem.*;

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
        RelativeLayout descriptionContainer;
        TextView timedate;
        ImageView durationIcon;
        TextView heading;
        Button btnExpand;

        CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            timedate = (TextView) itemView.findViewById(R.id.tvDateTime);
            heading = (TextView) itemView.findViewById(R.id.tvNetwork);
            btnExpand = (Button) itemView.findViewById(R.id.btnExpand);
            durationIcon = (ImageView) itemView.findViewById(R.id.ivDuration);
            descriptionContainer = (RelativeLayout) itemView.findViewById(R.id.rlDescription);
        }
    }

    public static class VideoCardViewHolder extends CardViewHolder {
        ImageView preview;

        VideoCardViewHolder(View itemView) {
            super(itemView);
            preview = (ImageView) itemView.findViewById(R.id.ivPreview);
        }
    }


    @Override
    public CardViewAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (true/*viewType == LAYOUT_VIDEO*/) {
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
        if (holder.getItemViewType() == LAYOUT_VIDEO) {
            VideoCardViewHolder videoHolder = (VideoCardViewHolder) holder;
            if (items.get(position).getType() == CardItem.TYPE_PIETCAST) {
                //holder.heading.setText(context.getString(R.string.pietcast));
            } else if (items.get(position).getType() == CardItem.TYPE_STREAM) {
                //holder.heading.setText(context.getString(R.string.twitch));
            } else {
                //holder.heading.setText(context.getString(R.string.youtube));
            }
            if (items.get(position).getPreview() != null) {
                videoHolder.preview.setImageDrawable(items.get(position).getPreview());
            }
        } else {
            if (items.get(position).getType() == CardItem.TYPE_SOCIAL_MEDIA_FACEBOOK) {
                //holder.heading.setText(context.getString(R.string.facebook));
            } else if (items.get(position).getType() == CardItem.TYPE_SOCIAL_MEDIA_TWITTER) {
                //holder.heading.setText(context.getString(R.string.twitter));
            } else {
                //holder.heading.setText(context.getString(R.string.uploadplan));
            }
        }
        holder.title.setText(items.get(position).getTitle());
        holder.description.setText(items.get(position).getDescription());
        holder.timedate.setText(items.get(position).getDatetime());
        holder.durationIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_watch_later_black_24dp));
        holder.btnExpand.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
        holder.btnExpand.setOnClickListener(view -> {
            if (holder.descriptionContainer.getVisibility() == View.GONE) {
                holder.descriptionContainer.setVisibility(View.VISIBLE);
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_less_black_24dp));
            } else {
                holder.descriptionContainer.setVisibility(View.GONE);
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
            }
        });
        holder.cv.setCardBackgroundColor(Color.parseColor(getBackgroundColor(items.get(position).getType())));
    }

    private String getBackgroundColor(int type) {
        switch (type) {
            case TYPE_VIDEO:
            case TYPE_STREAM:
                return "#ef5350";
            case TYPE_PIETCAST:
                return "#5c6bc0";
            case TYPE_SOCIAL_MEDIA_FACEBOOK:
            case TYPE_SOCIAL_MEDIA_TWITTER:
                return "#42a5f5";
            case TYPE_UPLOAD_PLAN:
                return "#26a69a";
            case TYPE_DEFAULT:
            default:
                return "#bdbdbd";
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getType() <= 3) {
            return LAYOUT_VIDEO;
        }
        return LAYOUT_SOCIAL;
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
