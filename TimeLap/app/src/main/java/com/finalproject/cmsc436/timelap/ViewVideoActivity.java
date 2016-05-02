package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ViewVideoActivity extends AppCompatActivity {

    VideoView vid;
    AnimationDrawable anim = new AnimationDrawable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        final ImageView animation = (ImageView) findViewById(R.id.imageAnim);
        //Get a fire base ref to get the photos and decode them and then send them to the surface view
        //Need to set up slideshow in andriod for the photos
        Firebase myFirebaseRef = new Firebase("https://timelap.firebaseio.com/");
        Firebase imageRef = myFirebaseRef.child("Images");
        String image_id = getIntent().getStringExtra("Key");
        String user_id = getIntent().getStringExtra("User");
        Firebase users = imageRef.child(user_id);
        Firebase image = users.child(image_id);
        System.out.println("HERE!!");
        image.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int x = 0;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    System.out.println("ID " + postSnapshot.getKey() + " " + postSnapshot.getValue());
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
        //GET ALL THE IMAGES
//        ArrayList<String> images;
//        for(int x = 0; x < images.size(); x++) {
            //decode all the photos
            //Make drawables
            //Drawable d = new BitmapDrawable(getResources(), bitmap);
//        }
//        AnimationDrawable anim = new AnimationDrawable();
//        anim.addFrame(d,2);


        //if you want the animation to loop, set false
//        anim.setOneShot(false);
//        anim.start();


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
