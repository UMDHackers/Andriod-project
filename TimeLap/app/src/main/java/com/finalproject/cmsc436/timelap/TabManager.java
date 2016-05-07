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
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Chat");

        //ImageView Setup
        ImageView homeIcon = new ImageView(this);
        homeIcon.setImageResource(R.drawable.home);
        ImageView profileIcon = new ImageView(this);
        profileIcon.setImageResource(R.drawable.profile);
        ImageView chatIcon = new ImageView(this);
        chatIcon.setImageResource(R.drawable.chat);

//        tab1.setIndicator(homeIcon);
//        profileIntent.putExtra("uid", getIntent().getStringExtra("uid"));
//        tab1.setContent(new Intent(this, GeneralPageActivity.class));

        Intent generalIntent = new Intent(this, GeneralPageActivity.class);
        generalIntent.putExtra("uid", getIntent().getStringExtra("uid"));
        tab1.setIndicator(homeIcon);
        tab1.setContent(generalIntent);

        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra("uid", getIntent().getStringExtra("uid"));
        tab2.setIndicator(profileIcon);
        tab2.setContent(profileIntent);

        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("uid", getIntent().getStringExtra("uid"));
        tab3.setIndicator(chatIcon);
        tab3.setContent(chatIntent);

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
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
