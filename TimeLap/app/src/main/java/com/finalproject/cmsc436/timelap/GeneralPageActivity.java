package com.finalproject.cmsc436.timelap;


import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.firebase.client.Firebase;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    String imageEncoded;
    List<String> imagesEncodedList;
    public static final String CAMERA_IMAGE_BUCKET_ID = String.valueOf(CAMERA_IMAGE_BUCKET_NAME.toLowerCase().hashCode());
    private Firebase mFirebaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_page);

        //Here is where the firebase will download the photos
        //Firebase ref = mFirebaseRef.child("mainpage");
        //Get all the childern of the mainpage
        //loop through the strings and decode them decodeBase64() AND ADD THEM TO THE GRID VIEW





        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this, mThumbnailsIdsPhotos));
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://timelap.firebaseio.com/");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Create an Intent to start the ImageViewActivity
                Intent intent = new Intent(GeneralPageActivity.this,
                        ViewVideoActivity.class);

                //HERE GET THE ID OF THE FULL VIDEO SO THAT YOU CAN ACCESS IT LATER, FROM MAIN PAGE FULL VIDEOS
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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select 5 Pictures"), 3645);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3645 && resultCode == RESULT_OK && data != null) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            imagesEncodedList = new ArrayList<String>();
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    Log.i("LOG", "URI " +  uri.toString());
                    // Get the cursor
                }
                Log.v("LOG_TAG", "Selected Images " + mArrayUri.size());
               upload(mArrayUri);
            }
            Toast.makeText(this, "uploaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry video not uploadable", Toast.LENGTH_SHORT).show();
        }


    }
    public void upload(ArrayList<Uri> urisArrayList) {
        CovertToBase64 downloadTask = new CovertToBase64();
        downloadTask.execute(urisArrayList);
        try {
            String[] encoded = downloadTask.get();
     //       SendToFireBase task = new SendToFireBase();
     //       task.execute(encoded);
     //      task.get();

            //Set up a async task to send the files to the
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public class SendToFireBase extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params) {
            //Send the photos to the firebase
            Firebase pdir = mFirebaseRef.child("photos");
            Firebase mainpage = mFirebaseRef.child("mainpage");
            Map<Integer, String> photos = new HashMap<Integer, String>();
            photos.put(0, params[0]);
            photos.put(1, params[1]);
            photos.put(2, params[2]);
            photos.put(3, params[3]);
            photos.put(4, params[4]);
            //Some how get the users email!
            //Add all the photos to the ("general photos section") YAY! REDUNDANCY! WITH THE PUSH
            //Firebase user = pdir.child(user_string);
            //user.add to the IDS OF THE MAIN FULL PHOTOS LOCATION
            //mainpage.add new image string to the front page with a link to the other photos in the all photos

            return null;
        }
    }

    protected void onResume() {
        super.onResume();
    }
    public class CovertToBase64 extends AsyncTask<ArrayList<Uri>, Void, String[]> {
        @Override
        protected String[] doInBackground(ArrayList<Uri>... params) {
            try {
                String[] array = new String[5];
                for(int x = 0; x < 5; x++) {
                    String path = params[0].get(x).getLastPathSegment();
                    path = path.substring(path.indexOf("/"));
                    path = "/storage/emulated/0/document" + path;
                    System.out.println(path);
                    //InputStream inputStream = new FileInputStream("/storage/emulated/0/document/" + path);
                    Bitmap bMap = BitmapFactory.decodeFile(path);
                    String tmp = encodeToBase64(bMap, Bitmap.CompressFormat.JPEG, 100);
                    Log.i("images " +x , tmp);
                    array[x] = tmp;
                }
                return array;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
