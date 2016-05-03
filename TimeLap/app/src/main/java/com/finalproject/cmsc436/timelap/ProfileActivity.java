package com.finalproject.cmsc436.timelap;

import android.content.ClipData;
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
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int UPLOAD_PROFILE_IMAGE = 1;
    private static final int SUCCESS = 1;


    private String mUserID, mUsername, mEmail;

    private HashMap<String, Bitmap> mThumbnailsIdsPhotos = new HashMap<String, Bitmap>();
    private HashMap<String, String> mThumbnailsIdsUsers = new HashMap<String, String>();
    List<String> imagesEncodedList;
    private  GridView gridView;
    private ArrayList<String> mkey = new ArrayList<String>();
    private ArrayList<Bitmap> list = new ArrayList<Bitmap>();
    private ImageView mProfileImage;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Got through main thumbnails ones and add the photos based on user, so go through whole list of images
        list.clear();
        // initialize Firebase
        mFirebaseRef = new Firebase("https://timelap.firebaseio.com");

        // Retrieve the uid from previous activities intent
        mUserID = getIntent().getStringExtra("uid");

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

        gridView = (GridView) findViewById(R.id.gridView);
        //gridView.setAdapter(new ImageAdapter(this, mProfileIds));
        Firebase thumbs  = mFirebaseRef.child("FrontPage");
        Firebase user = mFirebaseRef.child(mUserID);
        thumbs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String user = (String) postSnapshot.child("User").getValue();
                    if(mUserID.equals(user)) {
                        String temp = (String) postSnapshot.child("Encoded").getValue();
                        Bitmap temp_bit = decodeBase64(temp);
                        String key = (String) postSnapshot.getKey();
                        mThumbnailsIdsPhotos.put(key, temp_bit);
                        mThumbnailsIdsUsers.put(key, user);
                        mkey.add(key);
                        list.add(temp_bit);
                    }
                }
                System.out.println("LIST SIZE " + list.size());
                gridView.setAdapter(new ImageAdapter(ProfileActivity.this, list));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(ProfileActivity.this,
                        ViewVideoActivity.class);

                //HERE GET THE ID OF THE thubmnail clicked
                // Add the ID of the thumbnail to display as an Intent Extra
                //intent.putExtra(EXTRA_RES_ID, videoPaths.get(position));
                //GET THE IMAGE AND THEN IDS (USERS AND PHOTOIDS) WITH THE POSTIONS
                String temp_key = mkey.get(position);
                intent.putExtra("Key", temp_key);
                intent.putExtra("User", mUserID);

                // Start the ImageViewActivity
                startActivity(intent);
            }
        });

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
//            photos.put("0", params[0]);
//            photos.put("1", params[1]);
//            photos.put("2", params[2]);
//            photos.put("3", params[3]);
//            photos.put("4", params[4]);
            for(int x =0 ; x <params.length; x++) {
                photos.put(x+"", params[x]);
            }
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
    @Override
    protected void onResume() {
        super.onResume();
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
    public class CovertToBase64 extends AsyncTask<ArrayList<Uri>, Void, String[]> {
        @Override
        protected String[] doInBackground(ArrayList<Uri>... params) {
            try {
                String[] array = new String[params[0].size()];
                for(int x = 0; x < params[0].size(); x++) {
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


