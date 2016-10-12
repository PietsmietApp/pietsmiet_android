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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.adapters.CardViewAdapter;
import de.pscom.pietsmiet.backend.FacebookPresenter;
import de.pscom.pietsmiet.backend.RssPresenter;
import de.pscom.pietsmiet.backend.TwitterPresenter;
import de.pscom.pietsmiet.util.DrawableFetcher;

import static de.pscom.pietsmiet.adapters.CardItem.CardItemType.TYPE_PIETCAST;
import static de.pscom.pietsmiet.adapters.CardItem.CardItemType.TYPE_TWITTER;
import static de.pscom.pietsmiet.adapters.CardItem.CardItemType.TYPE_UPLOAD_PLAN;
import static de.pscom.pietsmiet.adapters.CardItem.CardItemType.TYPE_VIDEO;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardViewAdapter adapter;
    private List<CardItem> cardItems = new ArrayList<>();

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
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Only for testing
        new Thread(() -> {
            ArrayList<CardItem> cardItems = new ArrayList<>();
            cardItems.add(new CardItem("PietCast #79 - Krötenwehr",
                    "Der erste Podcast nach unserer Pause und es gab super viel zu bereden. Wir haben über unseren Urlaub gesprochen. Darüber wie wir mit Hate und Flame umgehen. Warum Produktplatzierungen existieren und warum wir sie machen. Warum Maschinenbau ein geiler Studiengang ist und zu guter Letzt welche 5 Personen auf einer Non-Cheat Liste stehen würden. Ihr wisst nicht was das ist!",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://www.pietcast.de/pietcast/wp-content/uploads/2016/09/thumbnail-672x372.png"),
                    TYPE_PIETCAST));
            cardItems.add(new CardItem("HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3",
                    "HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3\nDauer: 30 Minuten",
                    new Date(),
                    DrawableFetcher.getDrawableFromUrl("http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg"),
                    TYPE_VIDEO));
            cardItems.add(new CardItem("Uploadplan am 11.09.2016",
                    "14:00 Uhr: Osiris\n15:00 Uhr: Titan 3\n16:00 Uhr: Gears of War 4\n18:00 Uhr: Mario Kart 8",
                    new Date(),
                    TYPE_UPLOAD_PLAN));
            cardItems.add(new CardItem("Dr.Jay auf Twitter",
                    "Wow ist das Bitter für #Hamilton Sorry for that :-( @LewisHamilton #MalaysiaGP",
                    new Date(),
                    TYPE_TWITTER));

            runOnUiThread(() -> {
                for (CardItem cardItem : cardItems) addNewCard(cardItem);
            });
        }).start();
        setupRecyclerView();

        new TwitterPresenter().onTakeView(this);
        new RssPresenter().onTakeView(this);
        new FacebookPresenter().onTakeView(this);
    }

    public void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);

        adapter = new CardViewAdapter(cardItems, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    public void addNewCard(CardItem item) {
        cardItems.add(item);
        Collections.sort(cardItems);
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
