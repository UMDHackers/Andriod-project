package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewVideoActivity extends AppCompatActivity {

    VideoView vid;
    AnimationDrawable anim = new AnimationDrawable();
    TextView mLikes;
    TextView mAuthor;
    Button mButton, mPause, mDownload;
    ListView mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);

        final ImageView animation = (ImageView) findViewById(R.id.imageAnim);
        mDownload = (Button) findViewById(R.id.download);
        mLikes = (TextView) findViewById(R.id.likes);
        mAuthor = (TextView) findViewById(R.id.Author);
        ImageButton mButton = (ImageButton) findViewById(R.id.button);
        mPause = (Button) findViewById(R.id.anim_button);
        mList = (ListView) findViewById(R.id.Comments);
        String[] values = new String[] {"@Yoni: Nice Video dude!", "@Parth: Where was this taken?", "@Ben: I think that's California", "@Parth: OK that makes sense", "@Stan: It was near the google building"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, android.R.id.text1 ,values);
        mList.setAdapter(adapter);

        //Get a fire base ref to get the photos and decode them and then send them to the surface view
        //Need to set up slideshow in andriod for the photos
        Firebase myFirebaseRef = new Firebase("https://timelap.firebaseio.com/");
        Firebase imageRef = myFirebaseRef.child("Images");
        final Firebase likeRef = myFirebaseRef.child("Likes");
        final String image_id = getIntent().getStringExtra("Key");
        final String user_id = getIntent().getStringExtra("User");
        Firebase users = imageRef.child(user_id);
        Firebase image = users.child(image_id);
        Firebase our_user = myFirebaseRef.child("users").child(user_id);

//        System.out.println("HERE!!");
        image.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int x = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //System.out.println("ID " + postSnapshot.getKey() + " " + postSnapshot.getValue());
                    String temp = (String) postSnapshot.getValue();
                    Bitmap temp_bit = decodeBase64(temp);
                    Drawable d = new BitmapDrawable(getResources(), temp_bit);
                    anim.addFrame(d, 200);
                    x++;
                }

                anim.setOneShot(false);
//                anim.start();
                animation.setBackground(anim);
                anim.start();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        final Firebase child_likes = likeRef.child(image_id);
        final Firebase likesRef = child_likes.child("Likes");

        our_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAuthor.setText("TimeLap Taken By: @" + ((String) dataSnapshot.child("username").getValue()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final Firebase user_like = likeRef.child(image_id);

        user_like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLikes.setText("Likes:" + ((String) dataSnapshot.child("Likes").getValue()));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mLikes.getText().toString();
                int x = Integer.parseInt(str.substring(str.indexOf(":") + 1)) + 1;
                user_like.child("Likes").setValue(x + "");
            }
        });
        mAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ViewVideoActivity.this, ProfileActivity.class);
                profileIntent.putExtra("uid", user_id);
                startActivity(profileIntent);
            }
        });
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPause.getText().toString().equals("Pause")) {
                    anim.stop();
                    mPause.setText("Play");
                    mDownload.setVisibility(View.VISIBLE);
                } else {
                    mDownload.setVisibility(View.INVISIBLE);
                    anim.setOneShot(false);
                    anim.start();
                    mPause.setText("Pause");
                }


            }
        });
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable d = anim.getFrame(0);
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Timelap.Picture."+user_id, "PICTURE");
                Toast.makeText(getApplicationContext(), "Downloaded", Toast.LENGTH_SHORT).show();
            }
        });
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
