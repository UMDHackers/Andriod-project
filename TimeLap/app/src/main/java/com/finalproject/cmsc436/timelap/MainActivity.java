package com.finalproject.cmsc436.timelap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class MainActivity extends AppCompatActivity {
    Button mLogin, mSignUp;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);




        mLogin = (Button) findViewById(R.id.login);
        mSignUp = (Button) findViewById(R.id.sign_up);
        mFirebaseRef = new Firebase("https://timelap.firebaseio.com");

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });




        // This part check if the user info is being remembered, login if so.
        SharedPreferences prefs = getSharedPreferences("UserData", 0);
        String saved_email = prefs.getString("email", "");
        String saved_password = prefs.getString("password", "");
        if (saved_email.compareTo("") != 0 && saved_password.compareTo("") !=0 && saved_email != null && saved_password != null){
            // The user info has already saved and verified before, now login.
            String UID = prefs.getString("uid", "");
            Intent i = new Intent(getApplicationContext(), TabManager.class);
            Log.d("tttt","test: " + saved_email + UID);
            i.putExtra("uid", UID);
            startActivity(i);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(){
        EditText emailTv = (EditText) findViewById(R.id.email);
        EditText passwordTv = (EditText) findViewById(R.id.password);

        final String email = emailTv.getText().toString();
        final String password = passwordTv.getText().toString();

        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Intent i = new Intent(getApplicationContext(), TabManager.class);
                i.putExtra("uid", authData.getUid());

                CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
                if (checkBox.isChecked() == true){
                    rememberLogin(email, password, authData.getUid());
                } else {
                    rememberLogin("", "", "");
                }
                startActivity(i);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "Your email or password is not correct.", Toast.LENGTH_LONG).show();
            }
        });
    }


    // This method saves user data in cache
    public void rememberLogin(String email, String password, String UID){
        SharedPreferences prefs = getSharedPreferences("UserData", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("uid", UID);
        editor.commit();
    }
}
