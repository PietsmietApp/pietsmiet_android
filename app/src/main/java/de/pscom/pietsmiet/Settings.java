package de.pscom.pietsmiet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;

import com.google.firebase.messaging.FirebaseMessaging;

import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Switch newsSwitch = (Switch) findViewById(R.id.newsSwitch);


        toolbar.setTitle(getString(R.string.title_activity_settings));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        boolean current = SharedPreferenceHelper.getSharedPreferenceBoolean(this, KEY_NEWS_SETTING, true);
        newsSwitch.setChecked(current);

        newsSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_NEWS_SETTING, isChecked);
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("uploadplan");
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
