package de.pscom.pietsmiet.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.pscom.pietsmiet.R;

/**
 * Created by Gorb98 on 11.09.2016.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder>{

    List<CardItem> items;

    public CardViewAdapter(List<CardItem> items){
        this.items = items;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView description;
        TextView timedate;
        ImageView icon;

        CardViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.tvTitle);
            description = (TextView)itemView.findViewById(R.id.tvDescription);
            timedate = (TextView)itemView.findViewById(R.id.tvTimeDate);
            icon = (ImageView)itemView.findViewById(R.id.ivIcon);
        }
    }

    @Override
    public CardViewAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CardViewAdapter.CardViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.description.setText(items.get(position).getDescription());
        holder.timedate.setText(items.get(position).getDatetime());
        holder.icon.setImageDrawable(items.get(position).getIcon());
        holder.cv.setCardBackgroundColor(Color.parseColor(getBackgroundColor(items.get(position).getType())));
    }

    private String getBackgroundColor(int type){
        if (type == 0){
            return "#FFFFFF";
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
