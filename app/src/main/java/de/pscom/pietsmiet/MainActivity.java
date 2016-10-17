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

import java.util.ArrayList;
import java.util.Date;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.adapters.CardViewAdapter;
import de.pscom.pietsmiet.backend.FacebookPresenter;
import de.pscom.pietsmiet.backend.PietcastPresenter;
import de.pscom.pietsmiet.backend.TwitterPresenter;
import de.pscom.pietsmiet.backend.UploadplanPresenter;
import de.pscom.pietsmiet.util.CardItemManager;
import de.pscom.pietsmiet.util.DrawableFetcher;
import de.pscom.pietsmiet.util.PsLog;

import static de.pscom.pietsmiet.util.CardItemManager.DISPLAY_SOCIAL;
import static de.pscom.pietsmiet.util.CardType.PIETCAST;
import static de.pscom.pietsmiet.util.CardType.TWITTER;
import static de.pscom.pietsmiet.util.CardType.UPLOAD_PLAN;
import static de.pscom.pietsmiet.util.CardType.VIDEO;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardViewAdapter adapter;
    private DrawerLayout mDrawer;
    private CardItemManager cardManager;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cardManager = new CardItemManager(this);

        //Only for testing
        new Thread(() -> {
            ArrayList<CardItem> cardItems = new ArrayList<>();
            cardItems.add(new CardItem("TESTCast #79 - Krötenwehr",
                    "Der erste Podcast nach unserer Pause und es gab super viel zu bereden. Wir haben über unseren Urlaub gesprochen. Darüber wie wir mit Hate und Flame umgehen. Warum Produktplatzierungen existieren und warum wir sie machen. Warum Maschinenbau ein geiler Studiengang ist und zu guter Letzt welche 5 Personen auf einer Non-Cheat Liste stehen würden. Ihr wisst nicht was das ist!",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"),
                    PIETCAST));
            cardItems.add(new CardItem("TESTHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    "HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"),
                    VIDEO));
            cardItems.add(new CardItem("TESTplan am 11.09.2016",
                    "14:00 Uhr: Osiris<br>15:00 Uhr: Titan 3<br>16:00 Uhr: Gears of War 4<br>18:00 Uhr: Mario Kart 8",
                    new Date(),
                    UPLOAD_PLAN));
            cardItems.add(new CardItem("TESTJay auf Twitter",
                    "Wow ist das Bitter für #Hamilton Sorry for that :-( @LewisHamilton #MalaysiaGP http://pietsmiet.de",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"),
                    TWITTER));

            runOnUiThread(() -> {
                for (CardItem cardItem : cardItems) addNewCard(cardItem);
                PsLog.v("Test cards geladen");
            });
        }).start();
        setupRecyclerView();

        new TwitterPresenter().onTakeView(this);
        new UploadplanPresenter().onTakeView(this);
        new PietcastPresenter().onTakeView(this);
        new FacebookPresenter().onTakeView(this);
    }

    public void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        adapter = new CardViewAdapter(cardManager.getAllCardItems(), this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    public void addNewCard(CardItem item) {
        cardManager.addCard(item);
    }

    public void updateAdapter() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_upload_plan:
                cardManager.displayOnlyCardsFromType(UPLOAD_PLAN);
                break;
            case R.id.nav_social_media:
                cardManager.displayOnlyCardsFromType(DISPLAY_SOCIAL);
                break;
            case R.id.nav_pietcast:
                cardManager.displayOnlyCardsFromType(PIETCAST);
                break;
            case R.id.nav_home:
                cardManager.displayAllCards();
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
