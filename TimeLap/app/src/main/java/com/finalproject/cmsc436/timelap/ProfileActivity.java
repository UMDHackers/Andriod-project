package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private final String TAG = "ProfileActivity";
    private final int UPLOAD_PROFILE_IMAGE = 1;

    private Firebase mFirebaseRef;
    private String mUserID, mUsername, mEmail;

    private HashMap<String, Bitmap> mThumbnailsIdsPhotos = new HashMap<String, Bitmap>();
    private HashMap<String, String> mThumbnailsIdsUsers = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Got through main thumbnails ones and add the photos based on user, so go through whole list of images

        // initialize Firebase
        mFirebaseRef = new Firebase("https://timelap.firebaseio.com");

        // Retrieve the uid from previous activities intent
//        mUserID = getIntent().getStringExtra("uid");

        // Set the TextView.
//        mFirebaseRef.child("users").child(mUserID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // NOTE: this is a callback method, you won't be able to save the data outside the scope.
//                mEmail = dataSnapshot.child("email").getValue() + "";
//                mUsername = (dataSnapshot.child("username").getValue() + "").toUpperCase();
//                TextView emailView = (TextView) findViewById(R.id.profile_email);
//                TextView usernameView = (TextView) findViewById(R.id.profile_username);
//                emailView.setText(mEmail);
//                usernameView.setText(mUsername);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                Toast.makeText(getApplicationContext(), "user info not found", Toast.LENGTH_LONG).show();
//            }
//        });


        ImageView profileImage = (ImageView) findViewById(R.id.user_img);


//
//        GridView gridView = (GridView) findViewById(R.id.gridView);
//        gridView.setAdapter(new ImageAdapter(this, m));
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//
//                //Create an Intent to start the ImageViewActivity
//                Intent intent = new Intent(ProfileActivity.this,
//                        ViewVideoActivity.class);
//                //same idea here as in the general page idea
//                // Add the ID of the thumbnail to display as an Intent Extra
//                intent.putExtra("POS", videoPaths.get(position));
//
//                // Start the ImageViewActivity
//                startActivity(intent);
//            }
//        });

        // if user selects the profile image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "profile image clicked");

                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }

                intent.setType("*/sdcard/Download");
                startActivityForResult(intent, UPLOAD_PROFILE_IMAGE);

                /*

                // retrieves current UID
                AuthData authData = mFirebaseRef.getAuth();
                Firebase userRef = mFirebaseRef.child("users").child(authData.getUid());

                Log.i(TAG, "the UID is " + authData.getUid());

                Bitmap bmp = BitmapFactory.decodeFile("/sdcard/Download/ben.jpg");
                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bYtE);
                bmp.recycle();

                byte[] byteArray = bYtE.toByteArray();
                String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

                userRef.child("fullName").setValue("Alan Turing");
                userRef.child("image").setValue(imageFile);

*/
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

        } else if (requestCode == UPLOAD_PROFILE_IMAGE && resultCode == RESULT_OK && data != null) {
            //File file = new File(data.getData().getPath());
            Log.i(TAG, "path is " + data.getData().getPath());



        } else {
            Toast.makeText(this, "Sorry video not uploadable", Toast.LENGTH_SHORT).show();
        }


    }


}
