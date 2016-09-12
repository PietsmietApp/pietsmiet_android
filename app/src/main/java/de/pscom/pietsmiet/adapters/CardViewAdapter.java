package de.pscom.pietsmiet.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.util.PsLog;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder>{

    public static final int LAYOUT_VIDEO = 0;
    public static final int LAYOUT_SOCIAL = 1;

    private List<CardItem> items;
    private Context context;

    public CardViewAdapter(List<CardItem> items, Context context){
        this.items = items;
        this.context = context;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView description;
        TextView timedate;
        TextView heading;
        ImageView icon;
        CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.tvTitle);
            description = (TextView)itemView.findViewById(R.id.tvDescription);
            timedate = (TextView)itemView.findViewById(R.id.tvDateTime);
            heading = (TextView)itemView.findViewById(R.id.tvNetwork);
            icon = (ImageView)itemView.findViewById(R.id.ivIcon);
        }
    }

    public static class VideoCardViewHolder extends CardViewHolder {
        ImageView preview;
        VideoCardViewHolder(View itemView) {
            super(itemView);
            preview = (ImageView)itemView.findViewById(R.id.ivPreview);
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
        if (holder.getItemViewType() == LAYOUT_VIDEO) {
            VideoCardViewHolder videoHolder = (VideoCardViewHolder)holder;
            if (items.get(position).getType() == CardItem.TYPE_PIETCAST){
                holder.heading.setText(context.getString(R.string.pietcast));
            }else if (items.get(position).getType() == CardItem.TYPE_STREAM){
                holder.heading.setText(context.getString(R.string.twitch));
            }else{
                holder.heading.setText(context.getString(R.string.youtube));
            }
            if (items.get(position).getPreview() != null) {
                videoHolder.preview.setImageDrawable(items.get(position).getPreview());
            }
        }else{
            if (items.get(position).getType() == CardItem.TYPE_SOCIAL_MEDIA_FACEBOOK){
                holder.heading.setText(context.getString(R.string.facebook));
            }else if (items.get(position).getType() == CardItem.TYPE_SOCIAL_MEDIA_TWITTER){
                holder.heading.setText(context.getString(R.string.twitter));
            }else{
                holder.heading.setText(context.getString(R.string.uploadplan));
            }
        }
        holder.title.setText(items.get(position).getTitle());
        holder.description.setText(items.get(position).getDescription());
        holder.timedate.setText(items.get(position).getDatetime());
        holder.icon.setImageDrawable(items.get(position).getIcon());
        //holder.cv.setCardBackgroundColor(Color.parseColor(getBackgroundColor(items.get(position).getType())));
    }

    private String getBackgroundColor(int type){
        if (type == 0){
            return "#43A047";
        }else if (type == 1){
            return "#FFAEAE";
        }else if (type == 2){
            return "#FFAEAE";
        }else if (type == 3){
            return "#FFAEAE";
        }else if (type == 4){
            return "#B2AEFF";
        }else{
            return "#FFAEF2";
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getType() <= 3){
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
