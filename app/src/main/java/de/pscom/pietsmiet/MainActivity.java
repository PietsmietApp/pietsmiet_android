package de.pscom.pietsmiet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import de.pscom.pietsmiet.adapters.CardItem;
import de.pscom.pietsmiet.adapters.CardViewAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

        //Only for testing
        new Thread(() -> {
            ArrayList<CardItem> cardItems = new ArrayList<>();
            cardItems.add(new CardItem("PietCast #79 - Krötenwehr", "Der erste Podcast nach unserer Pause und es gab super viel zu bereden. Wir haben über unseren Urlaub gesprochen. Darüber wie wir mit Hate und Flame umgehen. Warum Produktplatzierungen existieren und warum wir sie machen. Warum Maschinenbau ein geiler Studiengang ist und zu guter Letzt welche 5 Personen auf einer Non-Cheat Liste stehen würden. Ihr wisst nicht was das ist!",
                    "Montag, 25. Dezember", ContextCompat.getDrawable(this, R.drawable.ic_music_note_black_24dp), "http://www.pietcast.de/pietcast/wp-content/uploads/2016/09/thumbnail-672x372.png", CardItem.TYPE_PIETCAST));
            cardItems.add(new CardItem("HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3", "HOCKENHEIMRING-TRAINING 2/2 \uD83C\uDFAE F1 2016 #3\nDauer: 30 Minuten",
                    "Montag, 25. Dezember um 13.00 Uhr", ContextCompat.getDrawable(this, R.drawable.youtube), "http://img.youtube.com/vi/0g2knLku2MM/hqdefault.jpg", CardItem.TYPE_VIDEO));
            cardItems.add(new CardItem("Uploadplan vom 11.09.", "14:00 Uhr TTT\n15:00 Uhr TTT\n" + "16:00 Uhr TTT\n18:00 Uhr TTT\n20:00 Uhr TTT",
                    "Montag, 25. Dezember", ContextCompat.getDrawable(this, R.drawable.ic_assignment_black_24dp), CardItem.TYPE_UPLOAD_PLAN));
            cardItems.add(new CardItem("Dr.Jay auf Twitter", "Wow ist das Bitter für #Hamilton Sorry for that :-( @LewisHamilton #MalaysiaGP",
                    "Montag, 25. Dezember um 14:69 Uhr", ContextCompat.getDrawable(this, R.drawable.ic_assignment_black_24dp), CardItem.TYPE_SOCIAL_MEDIA_TWITTER));

            runOnUiThread(() -> showCardViewItems(cardItems));
        }).start();
    }

    public void showCardViewItems(ArrayList<CardItem> cardItems) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);

        CardViewAdapter adapter = new CardViewAdapter(cardItems, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
