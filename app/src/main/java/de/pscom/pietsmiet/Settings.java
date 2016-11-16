package de.pscom.pietsmiet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;

import de.pscom.pietsmiet.util.SharedPreferenceHelper;
import static de.pscom.pietsmiet.util.SharedPreferenceHelper.KEY_NEWS_SETTING;

public class Settings extends AppCompatActivity {
    private Switch newsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        newsSwitch = (Switch) findViewById(R.id.newsSwitch);


        newsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic("uploadplan");
                    Toast.makeText(Settings.this, "Subscribe", Toast.LENGTH_SHORT).show();
                    SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this,KEY_NEWS_SETTING,true);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("uploadplan");
                    Toast.makeText(Settings.this, "Unsubscribe", Toast.LENGTH_SHORT).show();
                    SharedPreferenceHelper.setSharedPreferenceBoolean(Settings.this,KEY_NEWS_SETTING,false);
                }
            }
        });



    }
}
