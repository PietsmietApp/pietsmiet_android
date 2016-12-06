package de.pscom.pietsmiet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Set;

import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Switch newsSwitch = (Switch) findViewById(R.id.newsSwitch);
        LinearLayout bgElement = (LinearLayout) findViewById(R.id.container);


        toolbar.setTitle("Einstellungen");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (SharedPreferenceHelper.getSharedPreferenceBoolean(this, KEY_NEWS_SETTING, true)) {
            newsSwitch.setChecked(true);
        } else {
            newsSwitch.setChecked(false);
        }

        newsSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
                Toast.makeText(Settings.this, "Subscribe", Toast.LENGTH_SHORT).show();
                SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_NEWS_SETTING, true);
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("uploadplan");
                Toast.makeText(Settings.this, "Unsubscribe", Toast.LENGTH_SHORT).show();
                SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this, KEY_NEWS_SETTING, false);
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
