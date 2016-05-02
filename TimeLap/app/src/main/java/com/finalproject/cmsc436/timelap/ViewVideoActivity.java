package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.firebase.client.Firebase;

import java.util.ArrayList;

public class ViewVideoActivity extends AppCompatActivity {

    VideoView vid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        ImageView animation = (ImageView) findViewById(R.id.imageAnim);
        //Get a fire base ref to get the photos and decode them and then send them to the surface view
        //Need to set up slideshow in andriod for the photos
        Firebase myFirebaseRef = new Firebase("https://timelap.firebaseio.com/");

        String image_id = getIntent().getStringExtra("id");
        String user_id = getIntent().getStringExtra("user");
        Firebase users = myFirebaseRef.child(user_id);
        Firebase image = users.child(image_id);
    }

}
