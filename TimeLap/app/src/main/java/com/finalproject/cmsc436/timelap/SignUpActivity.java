package com.finalproject.cmsc436.timelap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText mUsernameTv, mEmailTv, mPasswordTv, mPasswordAgainTv;
    private String mUsername, mEmail, mPassword, mPasswordAgain;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://timelap.firebaseio.com");


        mUsernameTv = (EditText) findViewById(R.id.enter_username);
        mEmailTv = (EditText) findViewById(R.id.enter_email);
        mPasswordTv = (EditText) findViewById(R.id.enter_password);
        mPasswordAgainTv = (EditText) findViewById(R.id.confirm_password);

        final Button createUser = (Button) findViewById(R.id.create_account);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();

            }
        });

    }

    public void signUp(){

        mUsername = mUsernameTv.getText().toString();
        mEmail = mEmailTv.getText().toString();
        mPassword = mPasswordTv.getText().toString();
        mPasswordAgain = mPasswordAgainTv.getText().toString();

        if (mPassword.compareTo(mPasswordAgain) != 0){
            Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_LONG).show();
            return;
        }

        mFirebaseRef.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Toast.makeText(getApplicationContext(), "Successfully created user account" + result, Toast.LENGTH_LONG).show();
                finish();

            }
            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "failed creating user account.", Toast.LENGTH_LONG).show();
            }
        });


    }

}
