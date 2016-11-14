package de.pscom.pietsmiet;

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
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.PsLog;
import de.pscom.pietsmiet.util.SecretConstants;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static de.pscom.pietsmiet.util.PostManager.DISPLAY_SOCIAL;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.TWITTER;
import static de.pscom.pietsmiet.util.PostType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.PostType.VIDEO;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private DrawerLayout mDrawer;
    private PostManager postManager;

    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        postManager = new PostManager(this);

        setupRecyclerView();

        //Navigation Drawer
        mDrawer = (DrawerLayout) findViewById(R.id.dl_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        refreshLayout.setOnRefreshListener(this::updateData);
        refreshLayout.setColorSchemeColors(R.color.pietsmiet);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
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

    public void addNewPosts(List<Post> items) {
        if (postManager != null) postManager.addPosts(items);
    }

    public void updateAdapter() {
        Observable.defer(() -> Observable.just(""))
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
                .subscribe(ignored -> {
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                );
    }

    private void updateData() {
        new TwitterPresenter().onTakeView(this);
        //new UploadplanPresenter().onTakeView(this);
        new PietcastPresenter().onTakeView(this);
        new FacebookPresenter().onTakeView(this);
        //if (BuildConfig.DEBUG) addTestingCards();
    }

    private void addTestingCards() {
        //Only for testing
        new Thread(() -> {
            ArrayList<Post> cardItems = new ArrayList<>();
            cardItems.add(new Post("TESTCast #79 - Krötenwehr",
                    "Der erste Podcast nach unserer Pause und es gab super viel zu bereden. Wir haben über unseren Urlaub gesprochen. Darüber wie wir mit Hate und Flame umgehen. Warum Produktplatzierungen existieren und warum wir sie machen. Warum Maschinenbau ein geiler Studiengang ist und zu guter Letzt welche 5 Personen auf einer Non-Cheat Liste stehen würden. Ihr wisst nicht was das ist!",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"),
                    PIETCAST));
            cardItems.add(new Post("HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    "HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"),
                    VIDEO));
            cardItems.add(new Post("Uploadplan am 11.09.2016",
                    "14:00 Uhr: Osiris<br>15:00 Uhr: Titan 3<br>16:00 Uhr: Gears of War 4<br>18:00 Uhr: Mario Kart 8",
                    new Date(),
                    UPLOAD_PLAN));
            cardItems.add(new Post("Dr.Jay auf Twitter",
                    "Wow ist das Bitter für #Hamilton Sorry for that :-( @LewisHamilton #MalaysiaGP http://pietsmiet.de",
                    new Date(),
                    TWITTER));

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
                postManager.displayOnlyPostsFromType(UPLOAD_PLAN);
                break;
            case R.id.nav_social_media:
                postManager.displayOnlyPostsFromType(DISPLAY_SOCIAL);
                break;
            case R.id.nav_pietcast:
                postManager.displayOnlyPostsFromType(PIETCAST);
                break;
            case R.id.nav_home:
                postManager.displayAllPosts();
                break;
            default:
                return false;
        }
        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
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
