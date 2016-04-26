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

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneralPageActivity extends AppCompatActivity {
    private ArrayList<Integer> mThumbnailsIdsPhotos = new ArrayList<Integer>(
            Arrays.asList(R.drawable.ic_action_name, R.drawable.ic_action_name,
                    R.drawable.ic_action_name, R.drawable.ic_action_name, R.drawable.ic_action_name)
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
                intent.putExtra(EXTRA_RES_ID, (int) id);

                // Start the ImageViewActivity
                startActivity(intent);
            }
        });


        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);




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
                intent.setType("video/*");
                startActivityForResult(intent, 3645);
            }
        });
        // MyActivity below should be your activity class name
//        mDBApi.getSession().startOAuth2Authentication(GeneralPageActivity.this);
    }

    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        //Firebase.setAndroidContext(this);
//        //mFirebaseRef = new Firebase("https://timelap.firebaseio.com");
//        //AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials( MY_ACCESS_KEY_ID, MY_SECRET_KEY ) );
//        if (requestCode == 3645 && resultCode == RESULT_OK && data != null) {
//            File file = new File(data.getData().getPath());
//            MediaStore.Video video = (MediaStore.Video) data.getExtras().get("data");
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//
//            //use one of overloaded setDataSource() functions to set your data source
//            MediaPlayer mp = MediaPlayer.create(this, Uri.parse(data.getData().toString()));
//
//            retriever.setDataSource(getApplicationContext(), Uri.fromFile(file));
//            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            long timeInMillisec = Long.parseLong(time);
//            try {
//                FileInputStream fileInputStream = new FileInputStream(data.getData().getPath());
//                DropboxAPI.Entry response = mDBApi.putFile("/video1", fileInputStream, timeInMillisec, null, null);
//                Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
//            } catch (Exception e) {
//
//            }
//
//            Toast.makeText(this, "uploaded", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Sorry video not uploadable", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }

    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
}
