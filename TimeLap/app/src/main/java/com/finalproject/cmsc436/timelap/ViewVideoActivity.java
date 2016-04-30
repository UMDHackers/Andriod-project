package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class ViewVideoActivity extends AppCompatActivity {

    VideoView vid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);
        //Get a fire base ref to get the photos and decode them and then send them to the surface view
        //Need to set up slideshow in andriod for the photos

    }

}
