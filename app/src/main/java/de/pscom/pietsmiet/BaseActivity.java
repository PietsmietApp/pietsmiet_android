package de.pscom.pietsmiet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.leakcanary.LeakCanary;

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this.getApplication());
        }
    }

    protected void setupToolbar(@Nullable String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (title != null && !title.isEmpty()) {
            mToolbar.setTitle(title);
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
