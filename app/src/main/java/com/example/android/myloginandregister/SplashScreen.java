package com.example.android.myloginandregister;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth auth;// firebase auth object
    FirebaseUser user; // firebase user Object
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// save activity on activity stack
        setContentView(R.layout.activity_splash_screen);// load UI

        auth=FirebaseAuth.getInstance();  // get auth instance and stored it in a auth object
        user=auth.getCurrentUser(); // get Current user From auth
        runThreadDelay();  // call runThreadDelay()  function
    }

    // runThreadDelay function
    private void runThreadDelay() {
        Thread myThread = new Thread() { // create Background Thread
            @Override
            public void run() {  //when thread created successfully run() method will be called
                try {
                    sleep(6000); // sleep background thread for s seconds

                    /**firebase library automatically handle user Login
                     * so when once user make login firebase stored it login session in auth object
                     * so from  above line we are getting auth object auth=FirebaseAuth.getInstance()
                     * and get user from auth object user=auth.getCurrentObject()
                     * so when some one already made login auth object will return current user
                     * if no login found auth object will return null
                     * so in bellow if statement we are checking the user is null or not
                     * and also checking the email is verified or not
                     *
                     * **/
                    if(user!=null && user.isEmailVerified()){// check login , and email must be verified
                        // if already login and email verified start main activity
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }else {
                        // if no login found or email is not verified start registration activity
                        startActivity(new Intent(SplashScreen.this,Registration.class));
                    }
                    finish(); // finish() remove splash activity from activity stack so when user press back button the application will never jump to this activity agaqin
                } catch (InterruptedException e) {// through exception when thread failed to sleep
                    e.printStackTrace();
                }
            }
        };
        myThread.start();// start background thread
    }
}