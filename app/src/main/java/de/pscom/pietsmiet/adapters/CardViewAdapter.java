package de.pscom.pietsmiet.adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.TimeUtils;
import de.pscom.pietsmiet.view.CardViewHolder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private static final int LAYOUT_THUMBNAIL = 0;
    private static final int LAYOUT_WIDE_IMAGE = 1;

    private final List<Post> items;
    private final Context context;

    public CardViewAdapter(List<Post> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_main, parent, false);
        return new CardViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Post currentItem = items.get(position);
        @PostType.AllTypes int currentType = currentItem.getPostType();

        holder.time.setText(TimeUtils.getTimeSince(currentItem.getDate(), context));
        holder.title.setText(currentItem.getTitle());
        holder.cv.setCardBackgroundColor(currentItem.getBackgroundColor());

        holder.btnExpand.setVisibility(GONE);
        holder.descriptionContainer.setVisibility(GONE);
        holder.thumbnail.setVisibility(VISIBLE);
        holder.wideImage.setVisibility(GONE);
        holder.text.setVisibility(GONE);

        if (currentType == PIETCAST) {
            holder.thumbnail.setImageResource(R.drawable.pietcast_placeholder);

            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.description.setText(Html.fromHtml(currentItem.getDescription()));
            }

            holder.btnExpand.setVisibility(VISIBLE);
            holder.btnExpand.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
            holder.btnExpand.setOnClickListener(view -> {
                if (holder.descriptionContainer.getVisibility() == GONE) {
                    holder.descriptionContainer.setVisibility(VISIBLE);
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_less_black_24dp));
                } else {
                    holder.descriptionContainer.setVisibility(GONE);
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
                }
            });
        } else if (currentType == VIDEO) {
            Drawable thumbnail = currentItem.getThumbnail();
            if (thumbnail != null) {
                holder.thumbnail.setImageDrawable(thumbnail);
            } else {
                holder.thumbnail.setVisibility(GONE);
            }
        } else if (currentType == TWITTER || currentType == FACEBOOK) {
            holder.thumbnail.setVisibility(GONE);

            Drawable thumbnail = currentItem.getThumbnail();
            if (thumbnail != null) {
                holder.wideImage.setVisibility(VISIBLE);
                holder.wideImage.setImageDrawable(thumbnail);
                holder.wideImage.setVisibility(VISIBLE);
            }

        }
        if (currentType == TWITTER || currentType == FACEBOOK || currentType == UPLOADPLAN) {
            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.text.setVisibility(VISIBLE);
                holder.text.setText(Html.fromHtml(currentItem.getDescription()));
            }
        }

        holder.itemView.setOnClickListener(ignored -> {
                    try {
                        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getUrl()));
                        context.startActivity(browserIntent);
                    } catch (ActivityNotFoundException | NullPointerException e) {
                        PsLog.w("Cannot open browser intent. Url was: " + currentItem.getUrl());
                        //Error Toast Notification todo evtl eigene Funktion in seperater Klasse ?
                        CharSequence errMsg = "Cannot open browser. Retry";
                        Toast errToast = Toast.makeText(context, errMsg, Toast.LENGTH_SHORT);
                        errToast.show();
                    }
                }
        );


    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isThumbnailView()) return LAYOUT_THUMBNAIL;
        else return LAYOUT_WIDE_IMAGE;
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
