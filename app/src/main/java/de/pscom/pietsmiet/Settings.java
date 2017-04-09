package de.pscom.pietsmiet;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.messaging.FirebaseMessaging;

import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;

public class Settings extends BaseActivity {
    PostManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch notifyUploadplanSwitch = (Switch) findViewById(R.id.notifyUploadplanSwitch);
        Switch notifyVideoSwitch = (Switch) findViewById(R.id.notifyVideoSwitch);
        Button btnClearCache = (Button) findViewById(R.id.btnClearCache);
        setupToolbar(getString(R.string.drawer_einstellungen));

        boolean currentUploadplan = SharedPreferenceHelper.getSharedPreferenceBoolean(this, KEY_NOTIFY_UPLOADPLAN_SETTING, true);
        notifyUploadplanSwitch.setChecked(currentUploadplan);

        boolean currentVideo = SharedPreferenceHelper.getSharedPreferenceBoolean(this, KEY_NOTIFY_VIDEO_SETTING, true);
        notifyVideoSwitch.setChecked(currentVideo);

        btnClearCache.setOnClickListener((btn)->{
            new DatabaseHelper(getBaseContext()).clearDB();
            PostManager.CLEAR_CACHE_FLAG = true;
        });

        notifyUploadplanSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_NOTIFY_UPLOADPLAN_SETTING, isChecked);
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("uploadplan");
            }

        });

        notifyVideoSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_NOTIFY_VIDEO_SETTING, isChecked);
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("video");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("video");
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
