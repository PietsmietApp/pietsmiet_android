package de.pscom.pietsmiet;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;

import com.google.firebase.messaging.FirebaseMessaging;

import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;

import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;

public class Settings extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Switch newsSwitch = (Switch) findViewById(R.id.newsSwitch);
        Button btnClearCache = (Button) findViewById(R.id.btnClearCache);
        setupToolbar(getString(R.string.drawer_einstellungen));


        boolean current = SharedPreferenceHelper.getSharedPreferenceBoolean(this, KEY_NEWS_SETTING, true);
        newsSwitch.setChecked(current);
        btnClearCache.setOnClickListener((btn)->{
            new DatabaseHelper(getBaseContext()).clearDB();
        });

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
