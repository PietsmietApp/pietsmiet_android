package de.pscom.pietsmiet.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.customtabsclient.CustomTabActivityHelper;

public abstract class LinkUtil {
    public static void openUrl(Activity context, String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            CustomTabActivityHelper.openCustomTab(
                    context,
                    builder.build(),
                    Uri.parse(url),
                    (activity, uri) -> activity.startActivity(new Intent(Intent.ACTION_VIEW, uri)));
        } catch (ActivityNotFoundException | NullPointerException e) {
            PsLog.e("Cannot open url. Url was: " + url);
        }
    }
}
