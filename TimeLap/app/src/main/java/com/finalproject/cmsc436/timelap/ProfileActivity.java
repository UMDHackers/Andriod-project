package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
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
        //Got through main thumbnails ones and add the photos based on user, so go through whole list of images

        TextView emailView = (TextView) findViewById(R.id.profile_email);

        emailView.setText(getIntent().getStringExtra("email"));

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this, mProfileIds));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Create an Intent to start the ImageViewActivity
                Intent intent = new Intent(ProfileActivity.this,
                        ViewVideoActivity.class);
                //same idea here as in the general page idea
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
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.setType("*/sdcard/Download");
                startActivityForResult(intent, 3645);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3645 && resultCode == RESULT_OK && data != null) {
            File file = new File(data.getData().getPath());
            Toast.makeText(this, "uploaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry video not uploadable", Toast.LENGTH_SHORT).show();
        }


    }


}
