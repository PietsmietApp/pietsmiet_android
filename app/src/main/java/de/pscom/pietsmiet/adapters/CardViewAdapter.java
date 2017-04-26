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
import android.widget.RelativeLayout;

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

    private static final String strOpeningUrlStr = "Opening URL...";
    private static final String strCannotOpenBrowserIntentUrlWas = "Cannot open browser intent. Url was: ";
    private static final String strUrlKonnteNichtGeoeffnetWerden = "URL konnte nicht geöffnet werden";

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
        holder.headlineContainer.setBackgroundColor(currentItem.getBackgroundColor(context));
        holder.text.setLinkTextColor(currentItem.getBackgroundColor(context));

        holder.time.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        holder.timeClockImage.setImageResource(R.drawable.ic_access_time_white_24dp);

        RelativeLayout.LayoutParams paramsTimeContainer =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsTimeContainer.removeRule(RelativeLayout.BELOW);
        paramsTimeContainer.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.rlImageContainer);
        holder.timeContainer.setLayoutParams(paramsTimeContainer);

        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        // WITHOUT EMBEDDED IMAGE BUT PROFILE IMAGE IN BG: TODO
        //if(currentItem.getProfilePictureUrl() == null && currentItem.getProfilePictureHDUrl() == null) {
        //    params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.rlImageContainer);
        //    params.addRule(RelativeLayout.ALIGN_TOP, R.id.rlImageContainer);
        //    params.removeRule(RelativeLayout.BELOW);
        //      holder.wideImage.setVisibility(VISIBLE);
        //      holder.wideImage.setImageDrawable(null);

        // WITHOUT ANY IMAGE
        if (currentItem.getThumbnailUrl() == null && currentItem.getThumbnailHDUrl() == null) {
            params.removeRule(RelativeLayout.ALIGN_TOP);
            params.removeRule(RelativeLayout.ALIGN_BOTTOM);
            params.addRule(RelativeLayout.BELOW, R.id.rlHeadlineContainer);
            holder.wideImage.setVisibility(GONE);
            holder.wideImage.setImageDrawable(null);
            holder.time.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.timeClockImage.setImageResource(R.drawable.ic_access_time_black_24dp);
            RelativeLayout.LayoutParams paramsTimeContainerChange =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsTimeContainerChange.addRule(RelativeLayout.BELOW, R.id.rlExpandableContainer);
            paramsTimeContainerChange.removeRule(RelativeLayout.ALIGN_BOTTOM);
            holder.timeContainer.setLayoutParams(paramsTimeContainerChange);
        } else {
            //WITH EMBEDDED IMAGE:
            params.removeRule(RelativeLayout.ALIGN_TOP);
            params.removeRule(RelativeLayout.ALIGN_BOTTOM);
            params.addRule(RelativeLayout.BELOW, R.id.rlImageContainer);
            holder.wideImage.setVisibility(VISIBLE);
            holder.wideImage.setImageDrawable(null);
        }
        // apply RULES
        holder.descriptionContainer.setLayoutParams(params);


        // Setup default visibilities as you never can trust the view holder
        holder.line.setVisibility(GONE);
        holder.username.setVisibility(GONE);
        holder.btnExpand.setVisibility(GONE);
        holder.descriptionContainer.setVisibility(VISIBLE);
        holder.expandableContainer.setVisibility(GONE);
        holder.expandedDescription.setVisibility(VISIBLE);
        holder.text.setVisibility(GONE);

        int resPostTypeImage;

        switch (currentType) {
            case PIETCAST:
                resPostTypeImage = R.drawable.ic_radio_white_24dp;
                break;
            case UPLOADPLAN:
                resPostTypeImage = R.drawable.ic_assignment_white_24dp;
                break;
            case NEWS:
                resPostTypeImage = R.drawable.ic_rss_feed_white_24dp;
                break;
            case PS_VIDEO:
                resPostTypeImage = R.drawable.ic_ondemand_video_white_24dp;
                holder.wideImage.setVisibility(GONE);
                break;
            case YOUTUBE:
                resPostTypeImage = R.drawable.ic_youtube_light_logo;
                setupImageViews(holder.wideImage, currentItem, holder);
                break;
            case TWITTER:
                resPostTypeImage = R.drawable.ic_twitter_social_icon_circle_white_24dp;
                holder.line.setVisibility(VISIBLE);
                holder.username.setVisibility(VISIBLE);
                holder.username.setText("@" + currentItem.getUsername()); // TODO: 23.04.2017
                setupImageViews(holder.wideImage, currentItem, holder);
                // Setup text for social media
                if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                    holder.text.setVisibility(VISIBLE);
                    holder.text.setText(Html.fromHtml(currentItem.getDescription()));
                }
                break;
            case FACEBOOK:
                resPostTypeImage = R.drawable.ic_facebook_white;
                setupImageViews(holder.wideImage, currentItem, holder);
                // Setup text for social media
                if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                    holder.text.setVisibility(VISIBLE);
                    holder.text.setText(Html.fromHtml(currentItem.getDescription()));
                }
                break;
            default:
                resPostTypeImage = R.drawable.ic_radio_white_24dp;
        }

        holder.postTypeLogo.setImageResource(resPostTypeImage);


        // Setup expand container and description
        if (currentType == UPLOADPLAN || currentType == NEWS || currentType == PIETCAST) {
            if (currentItem.getDescription() != null && !currentItem.getDescription().isEmpty()) {
                holder.expandedDescription.setText(Html.fromHtml(currentItem.getDescription()));
            } else {
                holder.expandedDescription.setVisibility(GONE);
            }

            holder.descriptionContainer.setVisibility(GONE);
            holder.btnExpand.setVisibility(VISIBLE);
            holder.btnExpand.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));

            holder.btnExpand.setOnClickListener(view -> {
                if (holder.expandableContainer.getVisibility() == GONE) {
                    holder.expandableContainer.setVisibility(VISIBLE);
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_less_black_24dp));
                } else {
                    holder.expandableContainer.setVisibility(GONE);
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_expand_more_black_24dp));
                }
            });
        }

        // Open card externally on click
        holder.itemView.setOnClickListener(ignored -> {
                    try {
                        context.showSnackbar(strOpeningUrlStr, Snackbar.LENGTH_SHORT);
                        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentItem.getUrl()));
                        context.startActivity(browserIntent);
                    } catch (ActivityNotFoundException | NullPointerException e) {
                        PsLog.w(strCannotOpenBrowserIntentUrlWas + currentItem.getUrl());
                        //Error Notification
                        context.showSnackbar(strUrlKonnteNichtGeoeffnetWerden);
                    }
                }
        );


    }

    private void setupImageViews(ImageView view, Post currentItem, CardViewHolder holder) {
        if (currentItem.getThumbnailUrl() != null || currentItem.getThumbnailHDUrl() != null) {
            Glide.with(context)
                    .load((SettingsHelper.shouldLoadHDImages(context) ?
                            currentItem.getThumbnailHDUrl() :
                            currentItem.getThumbnailUrl()))
                    .centerCrop()
                    .into(view);
        } else {
            view.setVisibility(GONE);
            Glide.clear(view);
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
