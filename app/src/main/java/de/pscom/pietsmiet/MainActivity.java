package de.pscom.pietsmiet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.adapters.CardViewAdapter;
import de.pscom.pietsmiet.backend.DatabaseHelper;
import de.pscom.pietsmiet.backend.FacebookPresenter;
import de.pscom.pietsmiet.backend.PietcastPresenter;
import de.pscom.pietsmiet.backend.TwitterPresenter;
import de.pscom.pietsmiet.backend.UploadplanPresenter;
import de.pscom.pietsmiet.backend.YoutubePresenter;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.service.MyFirebaseMessagingService;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.PostType;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import de.pscom.pietsmiet.util.SettingsHelper;
import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOADPLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;
import static de.pscom.pietsmiet.util.PostType.getDrawerIdForType;
import static de.pscom.pietsmiet.util.PostType.getPossibleTypes;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private DrawerLayout mDrawer;
    private PostManager postManager;
    private NavigationView mNavigationView;

    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postManager = new PostManager(this);

        setupRecyclerView();
        setupDrawer();


        int category = getIntent().getIntExtra(MyFirebaseMessagingService.EXTRA_TYPE, -1);
        if (PostType.getDrawerIdForType(category) != -1) {
            onNavigationItemSelected(mNavigationView.getMenu().findItem(getDrawerIdForType(category)));
            postManager.displayOnlyType(category);
        }

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        refreshLayout.setOnRefreshListener(this::updateData);
        refreshLayout.setColorSchemeColors(R.color.pietsmiet);


        SettingsHelper.loadAllSettings(this);
        if (SharedPreferenceHelper.getSharedPreferenceBoolean(this, KEY_NEWS_SETTING, true)) {
            FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("uploadplan");
        }

        new SecretConstants(this);

        new DatabaseHelper(this).displayPostsFromCache(this);

        updateData();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        adapter = new CardViewAdapter(postManager.getPostsToDisplay(), this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setupDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDrawer = (DrawerLayout) findViewById(R.id.dl_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
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

    public void addNewPosts(List<Post> items) {
        if (postManager != null) postManager.addPosts(items);
    }

    public void updateAdapter() {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> {
                            if (adapter != null) adapter.notifyDataSetChanged();
                            if (refreshLayout != null) refreshLayout.setRefreshing(false);
                        }
                );
    }

    public void scrollToTop() {
        if (layoutManager != null) layoutManager.scrollToPosition(0);
    }

    public void showError(String msg) {
        Observable.defer(() -> Observable.just(""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                );
    }

    private void updateData() {
        new TwitterPresenter(this);
        new UploadplanPresenter(this);
        new PietcastPresenter(this);
        new FacebookPresenter(this);
        new YoutubePresenter(this);
        //if (BuildConfig.DEBUG) addTestingCards();
    }

    private void addTestingCards() {
        //Only for testing
        new Thread(() -> {
            ArrayList<Post> cardItems = new ArrayList<>();
            cardItems.add(new Post.PostBuilder(PIETCAST)
                    .title("TESTCast #79 - Krötenwehr")
                    .description("Der erste Podcast nach unserer Pause und es gab super viel zu bereden. Wir haben über unseren Urlaub gesprochen. Darüber wie wir mit Hate und Flame umgehen. Warum Produktplatzierungen existieren und warum wir sie machen. Warum Maschinenbau ein geiler Studiengang ist und zu guter Letzt welche 5 Personen auf einer Non-Cheat Liste stehen würden. Ihr wisst nicht was das ist!")
                    .date(new Date())
                    .thumbnail(DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"))
                    .build());
            cardItems.add(new Post.PostBuilder(VIDEO)
                    .title("HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3")
                    .description("HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3")
                    .date(new Date())
                    .thumbnail(DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"))
                    .build());
            cardItems.add(new Post.PostBuilder(UPLOADPLAN)
                    .title("Uploadplan am 11.09.2016")
                    .description("14:00 Uhr: Osiris<br>15:00 Uhr: Titan 3<br>16:00 Uhr: Gears of War 4<br>18:00 Uhr: Mario Kart 8")
                    .date(new Date())
                    .thumbnail(DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"))
                    .build());
            cardItems.add(new Post.PostBuilder(TWITTER)
                    .title("Dr.Jay")
                    .description("Wow ist das Bitter für #Hamilton Sorry for that :-( @LewisHamilton #MalaysiaGP http://pietsmiet.de")
                    .date(new Date())
                    .thumbnail(DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"))
                    .build());

            runOnUiThread(() -> {
                addNewPosts(cardItems);
                PsLog.v("Test cards geladen");
            });
        }).start();
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

    @Override
    protected void onPause() {
        super.onPause();
        if (postManager != null) {
            new DatabaseHelper(this).insertPosts(postManager.getAllPosts(), this);
        }
    }
}
