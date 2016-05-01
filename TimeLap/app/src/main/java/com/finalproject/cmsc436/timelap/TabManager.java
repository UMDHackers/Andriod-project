package com.finalproject.cmsc436.timelap;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;

public class TabManager extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_manager);

        // create the TabHost that will contain the Tabs
        final TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("Home");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Profile");


        //ImageView Setup
        ImageView homeIcon = new ImageView(this);
        homeIcon.setImageResource(R.drawable.home);
        ImageView profileIcon = new ImageView(this);
        profileIcon.setImageResource(R.drawable.profile);


        tab1.setIndicator(homeIcon);
        tab1.setContent(new Intent(this, GeneralPageActivity.class));

        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra("uid", getIntent().getStringExtra("uid"));
        tab2.setIndicator(profileIcon);
        tab2.setContent(profileIntent);

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
