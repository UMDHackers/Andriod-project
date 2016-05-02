package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.firebase.client.AuthData;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int UPLOAD_PROFILE_IMAGE = 1;
    private static final int SUCCESS = 1;

    private Firebase mFirebaseRef;
    private String mUserID, mUsername, mEmail;

    private ArrayList<Integer> mProfileIds = new ArrayList<Integer>(
            Arrays.asList(R.drawable.street, R.drawable.mount,
                    R.drawable.star, R.drawable.sun)
    );
    private ArrayList<String> videoPaths = new ArrayList<String >(
            Arrays.asList("/sdcard/Download/City.mp4", "/sdcard/Download/Mountain.mp4",
                    "/sdcard/Download/Stars.mp4", "/sdcard/Download/Sun.mp4")
    );

    private ImageView mProfileImage;

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

        mFirebaseRef.child("users").child(mUserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // NOTE: this is a callback method, you won't be able to save the data outside the scope.
                mEmail = dataSnapshot.child("email").getValue() + "";
                mUsername = (dataSnapshot.child("username").getValue() + "").toUpperCase();

                TextView emailView = (TextView) findViewById(R.id.profile_email);
                TextView usernameView = (TextView) findViewById(R.id.profile_username);

                emailView.setText(mEmail);
                usernameView.setText("@" + mUsername);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "user info not found", Toast.LENGTH_LONG).show();
            }
        });

//        GridView gridView = (GridView) findViewById(R.id.gridView);
//        gridView.setAdapter(new ImageAdapter(this, mProfile));
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


        // initialize the view
        mProfileImage = (ImageView) findViewById(R.id.user_img);

        // attempt to download existing profile image
        DownloadProfileImage downloadProfileImageTask = new DownloadProfileImage();
        downloadProfileImageTask.execute();

        // if user selects the profile image
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "profile image clicked");

                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, UPLOAD_PROFILE_IMAGE);
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

            if (data.getData() != null) {
                String filePath = data.getData().getPath();
                Log.i(TAG, "path is " + data.getData().getPath());

                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    String encodedImage = encodeToBase64(selectedImage, Bitmap.CompressFormat.JPEG, 100);

                    UploadProfileImage uploadProfileImageTask = new UploadProfileImage();
                    uploadProfileImageTask.execute(encodedImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "no image selected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Sorry video not uploadable", Toast.LENGTH_SHORT).show();
        }


    }

    /*
     * Used to upload profile images asynchronously
     */
    public class UploadProfileImage extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {

            String encodedImage = params[0];

            // send the photos to the firebase
            AuthData authData = mFirebaseRef.getAuth();
            Firebase userRef = mFirebaseRef.child("users").child(authData.getUid());

            userRef.child("image").setValue(encodedImage);

            return SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == SUCCESS) {

                // if profile image was successfully uploaded, download it to verify
                DownloadProfileImage downloadProfileImageTask = new DownloadProfileImage();
                downloadProfileImageTask.execute();
            }

        }
    }

    /*
     * Used to download profile image asynchronously
     */
    public class DownloadProfileImage extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            final AuthData authData = mFirebaseRef.getAuth();
            Firebase userRef = mFirebaseRef.child("users").child(authData.getUid());
            Firebase imageRef = userRef.child("image");

            // create the Firebase listener to retrieve data
            imageRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    // retrieves the encoded image string and converts
                    String encodedImage = (String) snapshot.getValue();

                    // sets the profile image if there is one
                    if (encodedImage != null) {
                        Bitmap image = decodeBase64(encodedImage);
                        mProfileImage.setImageBitmap(image);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

            return null;
        }
    }

    /*
     * Used for encoding and decoing bitmaps to base64 strings
     */
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


}


