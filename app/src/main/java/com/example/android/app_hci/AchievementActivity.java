package com.example.android.app_hci;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AchievementActivity extends AppCompatActivity {

    private TextView ShowText;
    private ProgressBar pb;
    private int progressBarValue;

    //Setting a listener for bottom menu
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(AchievementActivity.this.getApplication(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                case R.id.navigation_viewStory:
                    Intent intent2 = new Intent(AchievementActivity.this.getApplication(), ViewStoryActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
//                case R.id.navigation_storylist:
//                    mTextMessage.setText(R.string.title_storylist);
//                    return true;
                case R.id.navigation_stars:
                    Intent intent4 = new Intent(AchievementActivity.this.getApplication(), AchievementActivity.class);
                    startActivity(intent4);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        pb = (ProgressBar) findViewById(R.id.progressBar4);
        ShowText = (TextView)findViewById(R.id.text_progressbar4);

        //Initialise bottom menu
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Display Progress bar
        progressBarValue = 30;
        pb.setProgress(progressBarValue);
        ShowText.setText(progressBarValue +"/"+pb.getMax());


    }
}
