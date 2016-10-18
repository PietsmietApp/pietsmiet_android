package de.pscom.pietsmiet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import de.pscom.pietsmiet.adapters.CardViewAdapter;
import de.pscom.pietsmiet.backend.FacebookPresenter;
import de.pscom.pietsmiet.backend.PietcastPresenter;
import de.pscom.pietsmiet.backend.TwitterPresenter;
import de.pscom.pietsmiet.backend.UploadplanPresenter;
import de.pscom.pietsmiet.generic.Post;
import de.pscom.pietsmiet.io.Managers;
import de.pscom.pietsmiet.io.caching.PostCache;
import de.pscom.pietsmiet.util.PostManager;
import de.pscom.pietsmiet.util.SecretConstants;

import static de.pscom.pietsmiet.util.PostManager.DISPLAY_SOCIAL;
import static de.pscom.pietsmiet.util.PostType.PIETCAST;
import static de.pscom.pietsmiet.util.PostType.UPLOAD_PLAN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardViewAdapter adapter;
    LinearLayoutManager layoutManager;
    private DrawerLayout mDrawer;
    private PostManager postManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation Drawer
        mDrawer = (DrawerLayout) findViewById(R.id.dl_root);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        Managers.initialize(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        postManager = new PostManager(this);

        //Only for testing
        /*new Thread(() -> {
            ArrayList<CardItem> cardItems = new ArrayList<>();
            cardItems.add(new CardItem("TESTCast #79 - Krötenwehr",
                    "Der erste Podcast nach unserer Pause und es gab super viel zu bereden. Wir haben über unseren Urlaub gesprochen. Darüber wie wir mit Hate und Flame umgehen. Warum Produktplatzierungen existieren und warum wir sie machen. Warum Maschinenbau ein geiler Studiengang ist und zu guter Letzt welche 5 Personen auf einer Non-Cheat Liste stehen würden. Ihr wisst nicht was das ist!",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://www.pietcast.de/pietcast/wp-content/uploads/2016/09/thumbnail-672x372.png"),
                    PIETCAST));
            cardItems.add(new CardItem("HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    "HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"),
                    VIDEO));
            cardItems.add(new CardItem("Uploadplan am 11.09.2016",
                    "14:00 Uhr: Osiris<br>15:00 Uhr: Titan 3<br>16:00 Uhr: Gears of War 4<br>18:00 Uhr: Mario Kart 8",
                    new Date(),
                    UPLOAD_PLAN));
            cardItems.add(new CardItem("Dr.Jay auf Twitter",
                    "Wow ist das Bitter für #Hamilton Sorry for that :-( @LewisHamilton #MalaysiaGP http://pietsmiet.de",
                    new Date(),
                    TWITTER));

            runOnUiThread(() -> {
                for (CardItem cardItem : cardItems) addNewPost(cardItem);
                PsLog.v("Test cards geladen");
            });
        }).start();*/

        //only for testing
        /*new Thread(() -> {
            List<Post> posts = new ArrayList<>();
            posts.add(new ThumbnailPost("[DEBUG] PietCast #79 - Krötenwehr",
                    "Der erste Podcast nach unserer Pause und es gab super viel zu bereden. Wir haben über unseren Urlaub gesprochen. Darüber wie wir mit Hate und Flame umgehen. Warum Produktplatzierungen existieren und warum wir sie machen. Warum Maschinenbau ein geiler Studiengang ist und zu guter Letzt welche 5 Personen auf einer Non-Cheat Liste stehen würden. Ihr wisst nicht was das ist!",
                    Types.PIETCAST.getName(),
                    new Date(),
                    "http://www.pietcast.de/pietcast/wp-content/uploads/2016/09/thumbnail-672x372.png"));
            posts.add(new ThumbnailPost("[DEBUG] HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    "HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    Types.VIDEO.getName(),
                    new Date(),
                    "http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"));
            posts.add(new Post("[DEBUG] Uploadplan am 11.09.2016",
                    "14:00 Uhr: Osiris<br>15:00 Uhr: Titan 3<br>16:00 Uhr: Gears of War 4<br>18:00 Uhr: Mario Kart 8",
                    Types.UPLOAD_PLAN.getName(),
                    new Date()));
            posts.add(new Post("[DEBUG] Dr.Jay auf Twitter",
                    "Wow ist das Bitter für #Hamilton Sorry for that :-( @LewisHamilton #MalaysiaGP http://pietsmiet.de",
                    Types.TWITTER.getName(),
                    new Date()));

            //save to files
            PostCache.setPosts(posts);

            posts = null;

            //load from files
            List<Post> posts2 = PostCache.getPosts();

            runOnUiThread(() -> {
                for (Post post : posts2) addNewPost(post.getCardItem());
            });
        }).start();*/

        new Thread(() -> {
            try {
                //noinspection Convert2streamapi
                for (Post post : PostCache.getPosts()) addNewPost(post);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        setupRecyclerView();

        new SecretConstants(this);

        new TwitterPresenter().onTakeView(this);
        new UploadplanPresenter().onTakeView(this);
        new PietcastPresenter().onTakeView(this);
        new FacebookPresenter().onTakeView(this);
    }

    public void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        adapter = new CardViewAdapter(postManager.getAllPosts(), this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void addNewPost(Post item) {
        if (postManager != null) postManager.addPost(item);
    }

    public void updateAdapter() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void scrollToTop() {
        if (layoutManager != null) layoutManager.scrollToPosition(0);
    }

    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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
            PostCache.setPosts(postManager.getAllPosts());
        }
    }
}
