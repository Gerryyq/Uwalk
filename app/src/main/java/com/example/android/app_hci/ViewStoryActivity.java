package com.example.android.app_hci;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class ViewStoryActivity extends FragmentActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private TextView ShowText;
    private ProgressBar pb;
    private int progressBarValue;
    private  TextView tv1;
    private SearchView searchView;
    private StoryListAdapter adapter;
    private ListView story;
    private TextView temp_text;
    private List<Story> storyList;

    //Setting a listener for bottom menu
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(ViewStoryActivity.this.getApplication(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                case R.id.navigation_viewStory:
                    Intent intent2 = new Intent(ViewStoryActivity.this.getApplication(), ViewStoryActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                case R.id.navigation_stars:
                    Intent intent4 = new Intent(ViewStoryActivity.this.getApplication(), AchievementActivity.class);
                    startActivity(intent4);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
            }
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        pb = findViewById(R.id.progressBar3);
        ShowText = findViewById(R.id.text_progressbar3);
        progressBarValue = 30;
        pb.setProgress(progressBarValue);
        ShowText.setText(progressBarValue +"/"+pb.getMax());

        //Adding item to list
        story = (ListView) findViewById(R.id.list_all_stories);
        storyList = new ArrayList<>();
        storyList.add(new Story(1, "Remaining days on Earth.. EP 1", "It begins like any other day until..", R.drawable.sample));
        storyList.add(new Story(2, "Two", "It begins like any other day until..", R.drawable.sample));
        storyList.add(new Story(3, "Remaining Three on Earth.. EP 1", "It begins like any other day until..", R.drawable.sample));
        storyList.add(new Story(4, "Remaining Four on Earth.. EP 1", "It begins like any other day until..", R.drawable.sample));
        storyList.add(new Story(5, "Remaining Five on Earth.. EP 1", "It begins like any other day until..", R.drawable.sample));

        adapter = new StoryListAdapter(getApplicationContext(), storyList);
        story.setAdapter(adapter);

        story.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Do something
                Intent intent5 = new Intent(ViewStoryActivity.this.getApplication(), WalkingActivity.class);
                startActivity(intent5);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getMenu().getItem(1).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // Initialise Search bar
        searchView = findViewById(R.id.search);
        searchView.setOnClickListener(this);
        searchView.setQueryHint("Search a story title");
        searchView.setOnQueryTextListener(this);



    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.search:
                onSearchRequested();
                break;
        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }

}
