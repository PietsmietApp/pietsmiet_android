package de.pscom.pietsmiet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
        ArrayList<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem("Neuer Pietcast", "Hallo heute ist ein neuer Pietcast erschienen. Blah Test Blah Blah :DDD \n Dauer: 2:50 Stunden",
                "11.09.2016 13:00 Uhr", getResources().getDrawable(R.drawable.ic_music_note_black_24dp), CardItem.TYPE_PIETCAST));
        cardItems.add(new CardItem("Neuer Uploadplan", "14:00 Uhr TTT\n15:00 Uhr TTT\n" + "16:00 Uhr TTT\n18:00 Uhr TTT\n20:00 Uhr TTT",
                "12.09.2016 13:00 Uhr", getResources().getDrawable(R.drawable.ic_assignment_black_24dp), CardItem.TYPE_UPLOAD_PLAN));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);

        CardViewAdapter adapter = new CardViewAdapter(cardItems);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
