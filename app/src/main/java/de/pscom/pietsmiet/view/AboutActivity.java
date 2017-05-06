package de.pscom.pietsmiet.view;

import android.os.Bundle;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

import de.pscom.pietsmiet.BuildConfig;
import de.pscom.pietsmiet.R;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupToolbar(getString(R.string.drawer_support));

        TextView version = (TextView) findViewById(R.id.tv_app_version);
        version.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME + (BuildConfig.DEBUG ? "-dev" : "")));

        LibsSupportFragment fragment = new LibsBuilder()
                .supportFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }
    }


}
