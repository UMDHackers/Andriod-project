package com.finalproject.cmsc436.timelap;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.firebase.client.Firebase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GeneralPageActivity extends AppCompatActivity {
    private ArrayList<Integer> mThumbnailsIdsPhotos = new ArrayList<Integer>(
            Arrays.asList(R.drawable.street, R.drawable.mount,
                    R.drawable.star, R.drawable.sun)
    );
    private ArrayList<String> videoPaths = new ArrayList<String >(
            Arrays.asList("*/sdcard/City.mp4", "/sdcard/Download/Mountain.mp4",
                    "/sdcard/Download/Stars.mp4", "/sdcard/Download/Sun.mp4")
    );

    protected static final String EXTRA_RES_ID = "POS";
    private Firebase mFirebaseRef;
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = String.valueOf(CAMERA_IMAGE_BUCKET_NAME.toLowerCase().hashCode());
    final static private String APP_KEY = "1lxkdtav23yh3nf";
    final static private String APP_SECRET = "g56fafra1jvn3dq";
    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_page);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this, mThumbnailsIdsPhotos));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Create an Intent to start the ImageViewActivity
                Intent intent = new Intent(GeneralPageActivity.this,
                        ViewVideoActivity.class);

                // Add the ID of the thumbnail to display as an Intent Extra
                intent.putExtra(EXTRA_RES_ID, videoPaths.get(position));

                // Start the ImageViewActivity
                startActivity(intent);
            }
        });



//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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

    protected void onResume() {
        super.onResume();
    }
}
