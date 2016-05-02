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
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
    private HashMap<String, Bitmap> mThumbnailsIdsPhotos = new HashMap<String, Bitmap>();
    private HashMap<String, String> mThumbnailsIdsUsers = new HashMap<String, String>();

    protected static final String EXTRA_RES_ID = "POS";
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    String imageEncoded;
    List<String> imagesEncodedList;
    public static final String CAMERA_IMAGE_BUCKET_ID = String.valueOf(CAMERA_IMAGE_BUCKET_NAME.toLowerCase().hashCode());
    private Firebase mFirebaseRef;
    private String mUsername;
    private  GridView gridView;
    private ArrayList<Bitmap> list = new ArrayList<Bitmap>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_page);

        //Here is where the firebase will download the photos
        //Firebase ref = mFirebaseRef.child("mainpage");
        //Get all the childern of the mainpage
        //loop through the strings and decode them decodeBase64() AND ADD THEM TO THE GRID VIEW
        String mUserID = getIntent().getStringExtra("uid");




        gridView = (GridView) findViewById(R.id.gridView);
//        Firebase thumbnails = mFirebaseRef.child("FrontPage");
//        for(int x = 0; x< size of the thumbnails; x++) {
//        get the thumbnails
//        decode the strings
//        mThumbnailsIdsPhotos.add();
//        MAKE A LIST OF THE IDS AND USR IDS
//        }
        //GET ALL THUMBNAILS
        mFirebaseRef = new Firebase("https://timelap.firebaseio.com/");
        Firebase.setAndroidContext(this);
        Firebase thumbs  = mFirebaseRef.child("FrontPage");
        thumbs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("postshot " + postSnapshot.getKey());
                    String temp = (String) postSnapshot.child("Encoded").getValue();
                    Bitmap temp_bit = decodeBase64(temp);
                    String user = (String) postSnapshot.child("User").getValue();
                    String key = (String) postSnapshot.getKey();
                    mThumbnailsIdsPhotos.put(key, temp_bit);
                    mThumbnailsIdsUsers.put(key, user);
                    list.add(temp_bit);
                }
                System.out.println("LIST SIZE " + list.size());
                gridView.setAdapter(new ImageAdapter(GeneralPageActivity.this, list));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
//        try {
//            Thread.sleep(4000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Create an Intent to start the ImageViewActivity
                Intent intent = new Intent(GeneralPageActivity.this,
                        ViewVideoActivity.class);

                //HERE GET THE ID OF THE thubmnail clicked
                // Add the ID of the thumbnail to display as an Intent Extra
                //intent.putExtra(EXTRA_RES_ID, videoPaths.get(position));
                //GET THE IMAGE AND THEN IDS (USERS AND PHOTOIDS) WITH THE POSTIONS

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
            SendToFireBase task = new SendToFireBase();
            task.execute(encoded);
            task.get();

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
            Firebase pdir = mFirebaseRef.child("Images");
            Firebase mainpage = mFirebaseRef.child("FrontPage");
            Map<String, String> photos = new HashMap<String, String>();
            photos.put("0", params[0]);
            photos.put("1", params[1]);
            photos.put("2", params[2]);
            photos.put("3", params[3]);
            photos.put("4", params[4]);
            AuthData authData = mFirebaseRef.getAuth();
            Map<String, String> thumbnail = new HashMap<String, String>();
            //Thumbnail
            thumbnail.put("Encoded", params[0]);
            thumbnail.put("User", authData.getUid());

            Firebase thumb = mainpage.push();
            thumb.setValue(thumbnail);
            String key = thumb.getKey();
            //Set up the main photo
            thumbnail.put("IMG_TAG", key);
            pdir.child(authData.getUid()).child(key).setValue(photos);


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
                    //System.out.println(path);
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
