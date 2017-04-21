package de.pscom.pietsmiet;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.firebase.messaging.FirebaseMessaging;

import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_NEWS_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_PIETCAST_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_UPLOADPLAN_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_QUALITY_IMAGE_LOAD_HD_SETTING;


public class Settings extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch notifyUploadplanSwitch = (Switch) findViewById(R.id.notifyUploadplanSwitch);
        Switch notifyVideoSwitch = (Switch) findViewById(R.id.notifyVideoSwitch);
        Switch notifyNewsSwitch = (Switch) findViewById(R.id.notifyNewsSwitch);
        Switch notifyPietcastSwitch = (Switch) findViewById(R.id.notifyPietcastSwitch);
        Spinner qualityLoadHDImagesSpinner = (Spinner) findViewById(R.id.qualityLoadHDImagesSpinner);
        Button btnClearCache = (Button) findViewById(R.id.btnClearCache);

        setupToolbar(getString(R.string.drawer_einstellungen));

        SettingsHelper.loadAllSettings(this);

        notifyUploadplanSwitch.setChecked(SettingsHelper.boolUploadplanNotification);
        notifyVideoSwitch.setChecked(SettingsHelper.boolVideoNotification);
        notifyNewsSwitch.setChecked(SettingsHelper.boolNewsNotification);
        notifyPietcastSwitch.setChecked(SettingsHelper.boolPietcastNotification);
        qualityLoadHDImagesSpinner.setSelection(SettingsHelper.intQualityLoadHDImages);

        btnClearCache.setOnClickListener((btn) -> {
            setResult(MainActivity.RESULT_CLEAR_CACHE);
            finish();
        });

        qualityLoadHDImagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position < 3) {
                    SharedPreferenceHelper.setSharedPreferenceInt(Settings.this, KEY_QUALITY_IMAGE_LOAD_HD_SETTING, position);
                } else {
                    ((Spinner) view).setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
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
