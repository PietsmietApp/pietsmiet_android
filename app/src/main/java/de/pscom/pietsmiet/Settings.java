package de.pscom.pietsmiet;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.messaging.FirebaseMessaging;

import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_QUALITY_IMAGE_FORCE_HD_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING;

public class Settings extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch notifyUploadplanSwitch = (Switch) findViewById(R.id.notifyUploadplanSwitch);
        Switch notifyVideoSwitch = (Switch) findViewById(R.id.notifyVideoSwitch);
        Switch notifyNewsSwitch = (Switch) findViewById(R.id.notifyNewsSwitch);
        Switch notifyPietcastSwitch = (Switch) findViewById(R.id.notifyPietcastSwitch);
        Switch qualityForceHDImagesSwitch = (Switch) findViewById(R.id.qualityForceHDImagesSwitch);
        Switch qualityWifiOnlyHDImagesSwitch = (Switch) findViewById(R.id.qualityWifiOnlyHDImagesSwitch);
        Button btnClearCache = (Button) findViewById(R.id.btnClearCache);

        setupToolbar(getString(R.string.drawer_einstellungen));

        SettingsHelper.loadAllSettings(this);
        //todo implement auto reset of corresponding image quality switch
        notifyUploadplanSwitch.setChecked(SettingsHelper.boolUploadplanNotification);
        notifyVideoSwitch.setChecked(SettingsHelper.boolVideoNotification);
        notifyNewsSwitch.setChecked(SettingsHelper.boolNewsNotification);
        notifyPietcastSwitch.setChecked(SettingsHelper.boolPietcastNotification);
        qualityForceHDImagesSwitch.setChecked(SettingsHelper.boolForceHDImages);
        qualityWifiOnlyHDImagesSwitch.setChecked(SettingsHelper.boolWifiOnlyHDImages);

        btnClearCache.setOnClickListener((btn) -> {
            new DatabaseHelper(getBaseContext()).clearDB();
            PostManager.CLEAR_CACHE_FLAG = true;
        });

        qualityForceHDImagesSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_QUALITY_IMAGE_FORCE_HD_SETTING, isChecked);
        });

        qualityWifiOnlyHDImagesSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_QUALITY_IMAGE_WIFI_ONLY_HD_SETTING, isChecked);
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

        notifyNewsSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_NOTIFY_NEWS_SETTING, isChecked);
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("news");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
            }
        });

        notifyPietcastSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_NOTIFY_PIETCAST_SETTING, isChecked);
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("pietcast");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("pietcast");
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
