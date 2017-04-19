package de.pscom.pietsmiet.adapters;

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
import de.pscom.pietsmiet.util.PostType.AllTypes;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.TimeUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.pscom.pietsmiet.util.PostType.FACEBOOK;
import static de.pscom.pietsmiet.util.PostType.NEWS;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.PS_VIDEO;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.YOUTUBE;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewHolder> {

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

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Post currentItem = items.get(position);
        @AllTypes int currentType = currentItem.getPostType();

        // Set basic information (title, time, color)
        holder.time.setText(TimeUtils.getTimeSince(currentItem.getDate(), context));
        holder.title.setText(currentItem.getTitle());
        holder.cv.setCardBackgroundColor(currentItem.getBackgroundColor());

        // Setup default visibilities
        holder.btnExpand.setVisibility(GONE);
        holder.descriptionContainer.setVisibility(GONE);
        holder.thumbnail.setVisibility(VISIBLE);
        holder.wideImage.setVisibility(GONE);
        holder.text.setVisibility(GONE);

        if (currentType == PIETCAST) {
            // Pietcast: Setup placeholder thumbnail, text in expandable description,
            // setup expandable container
            holder.thumbnail.setImageResource(R.drawable.pietcast_placeholder);

            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.description.setText(Html.fromHtml(currentItem.getDescription()));
            }

            // TEMP because of unavailable data about durations
            holder.ivDuration.setVisibility(GONE);
            holder.tvDuration.setVisibility(GONE);

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
        } else if (currentType == UPLOADPLAN || currentType == NEWS) {
            //Disable thumbnail for uploadplan
            holder.thumbnail.setVisibility(GONE);

            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.description.setText(Html.fromHtml(currentItem.getDescription()));
            }

            holder.ivDuration.setVisibility(GONE);
            holder.tvDuration.setVisibility(GONE);
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
        } else if (currentType == PS_VIDEO || currentType == YOUTUBE) {
            // Youtube: Setup video thumbnails
            Drawable thumbnail = currentItem.getThumbnail();
            if (thumbnail != null) holder.thumbnail.setImageDrawable(thumbnail);
            else holder.thumbnail.setVisibility(GONE);
        } else if (currentType == TWITTER || currentType == FACEBOOK) {
            // Social media: Setup wide image
            holder.thumbnail.setVisibility(GONE);

            Drawable thumbnail = currentItem.getThumbnail();
            if (thumbnail != null) {
                holder.wideImage.setVisibility(VISIBLE);
                holder.wideImage.setImageDrawable(thumbnail);
                holder.wideImage.setVisibility(VISIBLE);
            }
        }
        // Setup text for (Uploadplan) & social media
        if (currentType == TWITTER || currentType == FACEBOOK) {
            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.text.setVisibility(VISIBLE);
                holder.text.setText(Html.fromHtml(currentItem.getDescription()));
            }
        }

        // Open card externally on click
        holder.itemView.setOnClickListener(ignored -> {
                    try {
                        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getUrl()));
                        context.startActivity(browserIntent);
                    } catch (ActivityNotFoundException | NullPointerException e) {
                        PsLog.w("Cannot open browser intent. Url was: " + currentItem.getUrl());
                        //Error Toast Notification
                        CharSequence errMsg = "URL konnte nicht geöffnet werden";
                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                    }
                }
        );


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
