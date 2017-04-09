package de.pscom.pietsmiet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import de.pscom.pietsmiet.util.DatabaseHelper;
import de.pscom.pietsmiet.generic.EndlessScrollListener;
import de.pscom.pietsmiet.service.MyFirebaseMessagingService;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.PostType.getDrawerIdForType;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private DrawerLayout mDrawer;
    private PostManager postManager;
    private NavigationView mNavigationView;
    private EndlessScrollListener scrollListener;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fabToTop;
    private RecyclerView recyclerView;

    public final int NUM_POST_TO_LOAD_ON_START = 15;

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
        refreshLayout.setOnRefreshListener(()-> {postManager.fetchNewPosts();});
        refreshLayout.setColorSchemeColors(R.color.pietsmiet);

        // to Top Button init
        fabToTop = (FloatingActionButton) findViewById(R.id.btnToTop);
        fabToTop.setVisibility(View.INVISIBLE);
        fabToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
                fabToTop.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public  void onShown(FloatingActionButton fab) {
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


        SettingsHelper.loadAllSettings(this);

        if (SharedPreferenceHelper.getSharedPreferenceBoolean(this, KEY_NEWS_SETTING, true)) {
            FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("uploadplan");
        }

        new SecretConstants(this);
        new DatabaseHelper(this).displayPostsFromCache(this);

        //  moved to DatabaseHelper as final Code -> if(postManager.getAllPostsCount() < NUM_POST_TO_LOAD_ON_START) postManager.fetchNextPosts(NUM_POST_TO_LOAD_ON_START);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(PostManager.CLEAR_CACHE_FLAG) {
            postManager.clearPosts();
            PostManager.CLEAR_CACHE_FLAG = false;
            postManager.fetchNextPosts(NUM_POST_TO_LOAD_ON_START);
        }
    }


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
                // Add whatever code is needed to append new items to the bottom of the list
                //todo wenn laden fehlschlägt Button Retry hinzufügen, da der scrolllistener sonst nicht weiter versucht zu laden!
                postManager.fetchNextPosts( loadMoreItemsCount );

            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setupDrawer() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDrawer = (DrawerLayout) findViewById(R.id.dl_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
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
        };
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //todo sinnvolle Konzeption? überall erreichbar ? Sicherheit?
    public void setRefreshAnim(boolean val) {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                    if(refreshLayout != null) refreshLayout.setRefreshing(val);
                });
    }

    public void updateAdapter() {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                            if (adapter != null) adapter.notifyDataSetChanged();
                            if (refreshLayout != null && postManager != null) {
                                if(postManager.getAllPostsFetched()) {
                                    setRefreshAnim(false);
                                    postManager.resetFetchingEnded();
                                    //todo evtl woanders hin auslagern
                                }
                            }
                        }
                );
    }

    public void scrollToTop() {
        if (layoutManager != null) layoutManager.scrollToPosition(0);
    }

    public void showError(String msg) {
        Observable.defer(() -> Observable.just(""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        }
                );
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
                        scrollToTop();
                    } else aSwitch.setChecked(false);
                }

                break;
            case R.id.nav_help:
                startActivity(new Intent(MainActivity.this, About.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;
            default:
                return false;
        }
        // Close the navigation drawer
        mDrawer.closeDrawers();

        return true;
    }
}
