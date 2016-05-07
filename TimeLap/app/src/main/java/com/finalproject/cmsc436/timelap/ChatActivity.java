package com.finalproject.cmsc436.timelap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private Firebase mFirebaseRef;
    private ListView mListView;
    private String mUserID;
    private String mName;
    private Button mSend;
    private EditText mEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mUserID = getIntent().getStringExtra("uid");
        mFirebaseRef = new Firebase("https://timelap.firebaseio.com/");
        Firebase.setAndroidContext(this);
        final Firebase msgs = mFirebaseRef.child("Chat");
        mListView = (ListView) findViewById(R.id.chat);
        mSend = (Button) findViewById(R.id.Send);
        mEdit = (EditText) findViewById(R.id.message);

        mFirebaseRef.child("users").child(mUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot){
                mName = (dataSnapshot.child("username").getValue() + "").toUpperCase();
            }
            @Override
            public void onCancelled (FirebaseError firebaseError){

            }
         });
         msgs.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 ArrayList<String> messages = new ArrayList<String>();
                 for (DataSnapshot post : dataSnapshot.getChildren()) {
                     messages.add((String) post.child("Message").getValue());
                 }
                 ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, messages);
                 mListView.setAdapter(adapter);
             }

             @Override
             public void onCancelled(FirebaseError firebaseError) {

             }
         });
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEdit.getText().toString();
                if (!content.equals("") || !content.equals(" ")) {
                    HashMap<String, String> message = new HashMap<String, String>();
                    message.put("Message", mName + ": " + content);
                    mEdit.setText("");
                    msgs.push().setValue(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Enter Message", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
