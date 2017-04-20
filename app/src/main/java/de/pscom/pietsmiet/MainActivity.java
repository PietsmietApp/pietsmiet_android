package de.pscom.pietsmiet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import de.pscom.pietsmiet.adapters.CardViewAdapter;
import de.pscom.pietsmiet.generic.EndlessScrollListener;
import de.pscom.pietsmiet.model.TwitchStream;
import de.pscom.pietsmiet.service.MyFirebaseMessagingService;
import de.pscom.pietsmiet.util.CacheUtil;
import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.TwitchHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.PostType.getDrawerIdForType;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int RESULT_CLEAR_CACHE = 17;
    public static final int RESULT_RELOAD = 18;
    public static final int REQUEST_SETTINGS = 16;
    private static final String url_feedback = "https://goo.gl/forms/3q4dEfOlFOTHKt2i2";
    private static final String url_pietstream = "https://www.twitch.tv/pietsmiet";
    private static final String twitch_channel_id_pietstream = "pietsmiet";

    private CardViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private DrawerLayout mDrawer;

    private NavigationView mNavigationView;
    private EndlessScrollListener scrollListener;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fabToTop;
    private RecyclerView recyclerView;
    private MenuItem pietstream_banner;

    public PostManager postManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar(null);

        postManager = new PostManager(this);

        setupRecyclerView();
        setupDrawer();

        int category = getIntent().getIntExtra(MyFirebaseMessagingService.EXTRA_TYPE, -1);
        if (PostType.getDrawerIdForType(category) != -1) {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(getDrawerIdForType(category)));
            postManager.displayOnlyType(category);
        }

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        refreshLayout.setOnRefreshListener(() -> postManager.fetchNewPosts());
        refreshLayout.setProgressViewOffset(false, -130, 80); //fixme can't be the right solution
        refreshLayout.setColorSchemeColors(R.color.pietsmiet);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.pietsmiet, R.color.colorPrimaryDark);

        // Top Button init
        fabToTop = (FloatingActionButton) findViewById(R.id.btnToTop);
        fabToTop.setVisibility(View.INVISIBLE);
        fabToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
                fabToTop.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onShown(FloatingActionButton fab) {
                        super.onShown(fab);
                    }

                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        super.onHidden(fab);
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        // End Top Button init

        SettingsHelper.loadAllSettings(this);
        FirebaseMessaging.getInstance().subscribeToTopic("test");

        if (SettingsHelper.boolUploadplanNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("uploadplan");
        }
        if (SettingsHelper.boolVideoNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic("video");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("video");
        }
        if (SettingsHelper.boolNewsNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
        }
        if (SettingsHelper.boolPietcastNotification) {
            FirebaseMessaging.getInstance().subscribeToTopic("pietcast");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("pietcast");
        }


        new SecretConstants(this);

        reloadTwitchBanner();

        new DatabaseHelper(this).displayPostsFromCache(this);

        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
                PsLog.e("Uncaught Exception!", paramThrowable);
                System.exit(2); //Prevents the service/app from reporting to firebase crash reporting!
            });
        }
    }

    /**
     * Reloads the stream status and updates the banner in the SideMenu
     */
    private void reloadTwitchBanner() {
        // todo handle unsubscribe
        Observable<TwitchStream> obsTTV = new TwitchHelper().getStreamStatus(twitch_channel_id_pietstream);
        obsTTV.subscribe((stream) -> {
            if (stream != null) {
                pietstream_banner.setVisible(true);
            } else {
                pietstream_banner.setVisible(false);
            }
        }, (err) -> PsLog.e("Could not update Twitch status", err));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (postManager != null) postManager.clearSubscriptions();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (postManager != null) postManager.clearSubscriptions();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
        if (scrollListener != null) scrollListener.resetState();
    }

    @Override
    public void onResume() {
        super.onResume();
        SettingsHelper.loadAllSettings(this);
        reloadTwitchBanner();
    }

    /**
     * Returns the current instance of the PostManager.
     *
     * @return PostManager reference
     */
    public PostManager getPostManager() {
        return postManager;
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        adapter = new CardViewAdapter(postManager.getPostsToDisplay(), this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                postManager.fetchNextPosts(LOAD_MORE_ITEMS_COUNT);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setupDrawer() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        pietstream_banner = mNavigationView.getMenu().findItem(R.id.nav_pietstream_banner);

        mDrawer = (DrawerLayout) findViewById(R.id.dl_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                for (Integer item : PostType.getPossibleTypes()) {
                    // Iterate through every menu item and save it's state in a map
                    Switch checker = (Switch) mNavigationView.getMenu().findItem(getDrawerIdForType(item)).getActionView();
                    postManager.allowedTypes.put(item, checker.isChecked());
                }
                postManager.updateCurrentPosts();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                reloadTwitchBanner();
            }
        };
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void setRefreshAnim(boolean val) {
        Observable.just(val)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bool -> {
                    if (refreshLayout != null) refreshLayout.setRefreshing(bool);
                });
    }

    public void updateAdapter() {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                    if(recyclerView != null) recyclerView.getRecycledViewPool().clear();
                            if (adapter != null) adapter.notifyDataSetChanged();
                        }
                );
    }
    public void showError(String message){
        showError(message, Snackbar.LENGTH_LONG);
    }
    public void showError(String message, int length) {
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msg -> {
                    if(findViewById(R.id.main_layout) != null) {
                        Snackbar.make(findViewById(R.id.main_layout), msg, length).show();
                    } else {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_upload_plan:
            case R.id.nav_facebook:
            case R.id.nav_twitter:
            case R.id.nav_pietcast:
            case R.id.nav_video:
                for (int i : getPossibleTypes()) {
                    int id = getDrawerIdForType(i);
                    Switch aSwitch = ((Switch) mNavigationView.getMenu().findItem(id).getActionView());
                    if (id == item.getItemId()) {
                        aSwitch.setChecked(true);
                        postManager.displayOnlyType(i);
                        recyclerView.scrollToPosition(0);
                    } else aSwitch.setChecked(false);
                }
                break;
            case R.id.nav_feedback:
                Intent i_Browser = new Intent(Intent.ACTION_VIEW);
                i_Browser.setData(Uri.parse(url_feedback));
                startActivity(i_Browser);
                break;
            case R.id.nav_help:
                startActivity(new Intent(MainActivity.this, About.class));
                break;
            case R.id.nav_settings:
                startActivityForResult(new Intent(MainActivity.this, Settings.class), REQUEST_SETTINGS);
                break;
            case R.id.nav_pietstream_banner:
                Intent i_TwitchBrowser = new Intent(Intent.ACTION_VIEW);
                i_TwitchBrowser.setData(Uri.parse(url_pietstream));
                startActivity(i_TwitchBrowser);
                break;
            default:
                return false;
        }
        // Close the navigation drawer
        mDrawer.closeDrawers();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTINGS) {
            SettingsHelper.loadAllSettings(this);
            if (resultCode == RESULT_CLEAR_CACHE) {
                new DatabaseHelper(this).clearDB();
                CacheUtil.trimCache(this);
            }
            if (resultCode == RESULT_RELOAD || resultCode == RESULT_CLEAR_CACHE) {
                postManager.clearPosts();
                scrollListener.resetState();
                postManager.fetchNewPosts();
            }
        }
    }


}
