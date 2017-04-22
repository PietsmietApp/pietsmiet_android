package de.pscom.pietsmiet.adapters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.pscom.pietsmiet.MainActivity;
import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.PostType.AllTypes;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SettingsHelper;
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
    private final MainActivity context;

    public CardViewAdapter(List<Post> items, MainActivity context) {
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

        // Setup default visibilities as you never can trust the view holder
        holder.btnExpand.setVisibility(GONE);
        holder.descriptionContainer.setVisibility(GONE);
        holder.description.setVisibility(VISIBLE);
        holder.thumbnail.setVisibility(GONE);
        holder.thumbnail.setImageDrawable(null);
        holder.wideImage.setVisibility(GONE);
        holder.wideImage.setImageDrawable(null);
        holder.text.setVisibility(GONE);
        holder.ivDuration.setVisibility(GONE);
        holder.tvDuration.setVisibility(GONE);

        if (currentType == PIETCAST) {
            // Pietcast: Setup placeholder thumbnail, text in expandable description,
            holder.thumbnail.setVisibility(VISIBLE);
            holder.thumbnail.setImageResource(R.drawable.pietcast_placeholder);

            // TEMP because of unavailable data about durations
            holder.ivDuration.setVisibility(GONE);
            holder.tvDuration.setVisibility(GONE);

        }
        // Setup expand container and description
        if (currentType == UPLOADPLAN || currentType == NEWS || currentType == PIETCAST) {
            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.description.setText(Html.fromHtml(currentItem.getDescription()));
            } else {
                holder.description.setVisibility(GONE);
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
        } else if (currentType == PS_VIDEO || currentType == YOUTUBE) {
            // Youtube: Setup video thumbnails
            setupImageViews(holder.thumbnail, currentItem, holder);
        } else if (currentType == TWITTER || currentType == FACEBOOK) {
            // Social media: Setup wide image
            setupImageViews(holder.wideImage, currentItem, holder);

            // Setup text for social media
            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.text.setVisibility(VISIBLE);
                holder.text.setText(Html.fromHtml(currentItem.getDescription()));
            }
        }

        // Open card externally on click
        holder.itemView.setOnClickListener(ignored -> {
                    try {
                        context.showSnackbar("Opening URL...", Snackbar.LENGTH_SHORT);
                        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getUrl()));
                        context.startActivity(browserIntent);
                    } catch (ActivityNotFoundException | NullPointerException e) {
                        PsLog.w("Cannot open browser intent. Url was: " + currentItem.getUrl());
                        //Error Notification
                        context.showSnackbar("URL konnte nicht geöffnet werden");
                    }
                }
        );


    }

    private void setupImageViews(ImageView view, Post currentItem, CardViewHolder holder) {
        view.setVisibility(VISIBLE);
        if (currentItem.getThumbnailUrl() != null || currentItem.getThumbnailHDUrl() != null) {
            Glide.with(context)
                    .load((SettingsHelper.shouldLoadHDImages(context) ?
                            currentItem.getThumbnailHDUrl() :
                            currentItem.getThumbnailUrl()))
                    .centerCrop()
                    //fixme.placeholder(R.drawable.ic_cached_black_24dp)
                    .crossFade()
                    .into(view);
        } else {
            view.setVisibility(GONE);
            Glide.clear(holder.thumbnail);
            Glide.clear(holder.wideImage);
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
