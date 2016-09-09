package de.pscom.pietsmiet;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TwitterApiClient twitterApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupTwitter();
    }

    private void setupTwitter(){
        TwitterAuthConfig authConfig = new TwitterAuthConfig("px2E2wOhxNrs4tsr8JqojB2yp", "zyVTNh7x2BUCDChlsZ6OStSqhFBBI8nEBWDGKv2HXcIfMbmLJg");
        Fabric.with(this, new TwitterCore(authConfig));
        twitterApiClient = TwitterCore.getInstance().getGuestApiClient();
    }

    private void getTweets(){
        SearchService searchService = twitterApiClient.getSearchService();
        Call<Search> call = searchService.tweets("from:pietsmiet, " +
                        "OR from:kessemak2, " +
                        "OR from:jaypietsmiet, " +
                        "OR from:brosator, " +
                        "OR from:br4mm3n"
                , null, null, null, "recent", 20, null, null, null, false);
        call.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                for (Tweet tweet : result.data.tweets) {
                    Log.d("TAG", tweet.text);
                }
            }

            public void failure(TwitterException exception) {
                //Do something on failure
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
