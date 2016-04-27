package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity {
    private ArrayList<Integer> mProfileIds = new ArrayList<Integer>(
            Arrays.asList(R.drawable.street, R.drawable.mount,
                    R.drawable.star, R.drawable.sun)
    );
    private ArrayList<String> videoPaths = new ArrayList<String >(
            Arrays.asList("/sdcard/Download/City.mp4", "/sdcard/Download/Mountain.mp4",
                    "/sdcard/Download/Stars.mp4", "/sdcard/Download/Sun.mp4")
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this, mProfileIds));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Create an Intent to start the ImageViewActivity
                Intent intent = new Intent(ProfileActivity.this,
                        ViewVideoActivity.class);

                // Add the ID of the thumbnail to display as an Intent Extra
                intent.putExtra("POS", videoPaths.get(position));

                // Start the ImageViewActivity
                startActivity(intent);
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
