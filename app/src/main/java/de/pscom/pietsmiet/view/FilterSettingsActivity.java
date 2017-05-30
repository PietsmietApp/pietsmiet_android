package de.pscom.pietsmiet.view;

import android.os.Bundle;
import android.view.MenuItem;

import de.pscom.pietsmiet.R;


public class FilterSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_settings);

        setupToolbar("Video-Benachrichtigungen");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
