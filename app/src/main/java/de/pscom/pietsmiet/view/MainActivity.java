package de.pscom.pietsmiet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.pscom.pietsmiet.BuildConfig;
import de.pscom.pietsmiet.R;
import de.pscom.pietsmiet.adapter.CardViewAdapter;
import de.pscom.pietsmiet.customtabsclient.CustomTabActivityHelper;
import de.pscom.pietsmiet.generic.EndlessScrollListener;
import de.pscom.pietsmiet.generic.ViewItem;
import de.pscom.pietsmiet.json_model.twitchApi.TwitchStream;
import de.pscom.pietsmiet.presenter.PostPresenter;
import de.pscom.pietsmiet.repository.PostRepositoryImpl;
import de.pscom.pietsmiet.service.MyFirebaseMessagingService;
import de.pscom.pietsmiet.util.CacheUtil;
import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.util.FirebaseUtil;
import de.pscom.pietsmiet.util.LinkUtil;
import de.pscom.pietsmiet.util.NetworkUtil;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import de.pscom.pietsmiet.util.TwitchHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.PostType.getDrawerIdForType;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;
import static de.pscom.pietsmiet.util.PostType.getTypeForDrawerId;
import static de.pscom.pietsmiet.util.SettingsHelper.boolAppFirstRun;
import static de.pscom.pietsmiet.util.SettingsHelper.boolVideoNotification;
import static de.pscom.pietsmiet.util.SettingsHelper.isOnlyType;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_APP_FIRST_RUN;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NOTIFY_VIDEO_SETTING;

public class MainActivity extends BaseActivity implements MainActivityView, NavigationView.OnNavigationItemSelectedListener {
    public static final int RESULT_CLEAR_CACHE = 17;
    public static final int REQUEST_SETTINGS = 16;
    private CustomTabActivityHelper mCustomTabActivityHelper;

    private boolean CLEAR_CACHE_FLAG_DRAWER = false;

    private CardViewAdapter adapter;
    @BindView(R.id.dl_root)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    public EndlessScrollListener scrollListener;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.btnToTop)
    FloatingActionButton fabToTop;
    @BindView(R.id.cardList)
    RecyclerView recyclerView;
    private MenuItem pietstream_banner;

    private PostPresenter postPresenter;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SettingsHelper.loadAllSettings(this);
        setupToolbar(null);

        mCustomTabActivityHelper = new CustomTabActivityHelper();

        postPresenter = new PostPresenter(this,
                new PostRepositoryImpl(this),
                DatabaseHelper.getInstance(this.getApplicationContext()),
                new NetworkUtil(this.getApplicationContext()),
                this.getApplicationContext());

        setupRecyclerView();
        setupDrawer();

        int category = getIntent().getIntExtra(MyFirebaseMessagingService.EXTRA_TYPE, -1);
        if (PostType.getDrawerIdForType(category) != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt(FirebaseAnalytics.Param.ITEM_NAME, category);
            FirebaseAnalytics.getInstance(this).logEvent("notification_clicked", bundle);
            onNavigationItemSelected(mNavigationView.getMenu().findItem(getDrawerIdForType(category)));
        }

        refreshLayout.setOnRefreshListener(() -> postPresenter.fetchNewPosts());
        refreshLayout.setProgressViewOffset(false, -130, 80); //todo Find another way. Just added to support Android 4.x
        refreshLayout.setColorSchemeColors(R.color.pietsmiet);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.pietsmiet, R.color.colorPrimaryDark);

        // Top Button init
        fabToTop.setVisibility(View.INVISIBLE);
        fabToTop.setOnClickListener(new View.OnClickListener() { //Butter Knife useful?
            @Override
            public void onClick(View v) {
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() < 85) {
                    recyclerView.smoothScrollToPosition(0);
                } else {
                    recyclerView.scrollToPosition(0);
                }
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

        if (boolAppFirstRun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_video_notification)
                    .setPositiveButton(R.string.yes, (dialog, id) -> {
                        boolVideoNotification = true;
                        SharedPreferenceHelper.setSharedPreferenceBoolean(this, KEY_NOTIFY_VIDEO_SETTING, true);
                        FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_VIDEO, true);
                    })
                    .setNegativeButton(R.string.no, (dialog, id) -> {
                        boolVideoNotification = false;
                        SharedPreferenceHelper.setSharedPreferenceBoolean(this, KEY_NOTIFY_VIDEO_SETTING, false);
                        FirebaseUtil.setFirebaseTopicSubscription(FirebaseUtil.TOPIC_VIDEO, false);
                    });
            builder.create().show();

            // Set AppFirstRun to false //todo maybe position this in OnDestroy / OnPause, because of logic
            boolAppFirstRun = false;
            SharedPreferenceHelper.setSharedPreferenceBoolean(this, KEY_APP_FIRST_RUN, false);
        }

        FirebaseUtil.loadRemoteConfig();
        FirebaseUtil.setupTopicSubscriptions();
        FirebaseUtil.disableCollectionOnDebug(this.getApplicationContext());

        new SecretConstants(this);

        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
                PsLog.w("Uncaught Exception!", paramThrowable);
                System.exit(2); //Prevents the service/app from reporting to firebase crash reporting!
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (postPresenter != null) postPresenter.stopSubscriptions();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (postPresenter.getPostsToDisplay().isEmpty()) {
            // Load posts from db
            DatabaseHelper.getInstance(this).displayPostsFromCache(postPresenter);
        } else if ((exitTime - System.currentTimeMillis()) > (15 * 60 * 60 * 1000)) {
            // Auto reload posts if going back to activity after more than 15 minutes
            postPresenter.fetchNewPosts();
        }
        mCustomTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        exitTime = System.currentTimeMillis();
        mCustomTabActivityHelper.unbindCustomTabsService(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (postPresenter != null) postPresenter.stopSubscriptions();
        if (refreshLayout != null) refreshLayout.setRefreshing(false);
        if (scrollListener != null) scrollListener.resetState();
    }

    @Override
    public void onResume() {
        super.onResume();
        SettingsHelper.loadAllSettings(this);
        // Update adapter to refresh timestamps
        refreshAdapter();
    }

    private void setupRecyclerView() {
        adapter = new CardViewAdapter(postPresenter.getPostsToDisplay(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                postPresenter.fetchNextPosts();
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setupDrawer() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        pietstream_banner = mNavigationView.getMenu().findItem(R.id.nav_pietstream_banner);

        // Iterate through every menu item and save it's state
        //todo improve if for example a user just switched on off on -> dont clear cache
        for (Integer item : PostType.getPossibleTypes()) {
            if (mNavigationView != null) {
                Switch checker = (Switch) mNavigationView.getMenu().findItem(getDrawerIdForType(item)).getActionView();
                checker.setChecked(SettingsHelper.getSettingsValueForType(item));
                checker.setOnCheckedChangeListener((view, check) -> {
                    if (check)
                        CLEAR_CACHE_FLAG_DRAWER = true; //todo improve if for example a user just switched on off on -> dont clear cache
                    SharedPreferenceHelper.setSharedPreferenceBoolean(getBaseContext(), SettingsHelper.getSharedPreferenceKeyForType(item), checker.isChecked());
                });
            }
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                SettingsHelper.loadAllSettings(getBaseContext());
                if (CLEAR_CACHE_FLAG_DRAWER) {
                    clearCache();
                    postPresenter.fetchNewPosts();
                    CLEAR_CACHE_FLAG_DRAWER = false;
                } else {
                    postPresenter.updateSettingsFilters();
                    scrollListener.resetState();
                }
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

    public void refreshAdapter() {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                            if (recyclerView != null) recyclerView.getRecycledViewPool().clear();
                            if (adapter != null) adapter.notifyDataSetChanged();
                        }
                );
    }

    public void scrollToTop() {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                            if (recyclerView != null) recyclerView.scrollToPosition(0);
                        }
                );
    }

    /**
     * Reloads the stream status and updates the banner in the SideMenu
     */
    private void reloadTwitchBanner() {
        Observable<TwitchStream> obsTTV = new TwitchHelper().getStreamStatus(SettingsHelper.stringTwitchChannelIDPietstream);
        obsTTV.subscribe((stream) -> {
            if (stream != null) {
                pietstream_banner.setVisible(true);
            } else {
                pietstream_banner.setVisible(false);
            }
        }, (err) -> PsLog.e("Could not update Twitch status", err));
    }

    public void updateAdapterItemRange(int startPosition, int size) {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                            if (adapter != null) adapter.notifyItemRangeInserted(startPosition, size);
                        }
                );
    }

    public void setRefreshAnim(boolean val) {
        Observable.just(val)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bool -> {
                    if (refreshLayout != null) refreshLayout.setRefreshing(bool);
                });
    }

    public void showMessage(String message, int length, boolean retryLoadingButton, boolean fetchDirectionDown) {
        Observable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msg -> {
                    if (findViewById(R.id.main_layout) != null) {
                        Snackbar sb = Snackbar.make(findViewById(R.id.main_layout), msg, length);
                        if (retryLoadingButton) sb.setAction(R.string.info_retry, (view) -> {
                            scrollListener.resetState();
                            if (fetchDirectionDown) {
                                postPresenter.fetchNextPosts();
                            } else {
                                postPresenter.fetchNewPosts();
                            }
                        });
                        sb.show();
                    } else {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void showMessage(String message, int length) {
        showMessage(message, length, false, false);
    }

    private void clearCache() {
        DatabaseHelper.getInstance(this).clearDB();
        postPresenter.clearPosts();
        refreshAdapter();
        scrollListener.resetState();
        CacheUtil.trimCache(this);
        fabToTop.hide();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_upload_plan:
            case R.id.nav_facebook:
            case R.id.nav_twitter:
            case R.id.nav_pietcast:
            case R.id.nav_ps_news:
            case R.id.nav_video_ps:
            case R.id.nav_video_yt:
                if (((Switch) item.getActionView()).isChecked() && isOnlyType(getTypeForDrawerId(item.getItemId()))) {
                    for (int z : getPossibleTypes()) {
                        int id = getDrawerIdForType(z);
                        Switch aSwitch = ((Switch) mNavigationView.getMenu().findItem(id).getActionView());
                        aSwitch.setChecked(true);
                        recyclerView.scrollToPosition(0);
                    }
                } else {
                    for (int i : getPossibleTypes()) {
                        int id = getDrawerIdForType(i);
                        Switch aSwitch = ((Switch) mNavigationView.getMenu().findItem(id).getActionView());
                        if (id == item.getItemId()) {
                            aSwitch.setChecked(true);
                            recyclerView.scrollToPosition(0);
                        } else aSwitch.setChecked(false);
                    }
                }
                break;
            case R.id.nav_feedback:
                LinkUtil.openUrl(this, SettingsHelper.stringFeedbackUrl);
                break;
            case R.id.nav_help:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.nav_settings:
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), REQUEST_SETTINGS);
                break;
            case R.id.nav_pietstream_banner:
                LinkUtil.openUrlExternally(this, SettingsHelper.stringPietstreamUrl);
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
                clearCache();
                showMessage(getString(R.string.info_cleared_cache));
            } else {
                // Update adapter to refresh timestamps
                refreshAdapter();
            }
        }
    }


    @Override
    public void freshLoadingCompleted() {
        refreshAdapter();
        setRefreshAnim(false);

        FirebaseAnalytics.getInstance(this).logEvent(FirebaseUtil.EVENT_FRESH_COMPLETED, new Bundle());
    }

    @Override
    public void loadingNextCompleted(int startPosition, int itemCount) {
        updateAdapterItemRange(startPosition, itemCount);
        setRefreshAnim(false);

        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseUtil.PARAM_START_POSITION, startPosition);
        bundle.putInt(FirebaseUtil.PARAM_ITEM_COUNT, itemCount);
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseUtil.EVENT_NEXT_COMPLETED, bundle);
    }

    @Override
    public void loadingNewCompleted(int itemCount) {
        updateAdapterItemRange(0, itemCount);
        setRefreshAnim(false);
        scrollToTop();

        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseUtil.PARAM_ITEM_COUNT, itemCount);
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseUtil.EVENT_NEW_COMPLETED, bundle);
    }

    @Override
    public void noNetworkError() {
        showMessage(getString(R.string.error_no_network));
        scrollListener.resetState();
    }

    @Override
    public void loadingStarted() {
        setRefreshAnim(true);
    }

    @Override
    public void loadingFailed(String message, boolean fetchDirectionDown) {
        showMessage(message, Snackbar.LENGTH_INDEFINITE, true, fetchDirectionDown);
        setRefreshAnim(false);
    }

    @Override
    public void showMessage(String message) {
        showMessage(message, Snackbar.LENGTH_LONG, false, false);
    }
}
