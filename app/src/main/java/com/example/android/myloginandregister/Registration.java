package com.example.android.myloginandregister;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.android.myloginandregister.Fragments.LoginFragment;
import com.google.firebase.FirebaseApp;


// this activity do nothing just loads login and signUP fragments
public class Registration extends AppCompatActivity {
   FragmentManager fragmentManager; // fragment manager Variable
   FragmentTransaction fragmentTransaction;// fragment trsnsation variable
//when activity start loading on create function will call first
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// save activity to stack
        setContentView(R.layout.activity_registration); //load UI
        FirebaseApp.initializeApp(this);// initialize firebase this line i used when some one install application for the first time
        fragmentManager=getSupportFragmentManager(); // get fragment manager
        LoadFragment(new LoginFragment());//  call Load Fragment function
        // so from this line the application will load LoginFragment first
    }

    // load fragment function
    private void LoadFragment(Fragment fragment) { // argument fragment
        if (fragment != null) {// if fragment is not null
            fragmentTransaction=fragmentManager.beginTransaction();// start transaction from fragment manager
            fragmentTransaction.replace(R.id.fragment_container, fragment);// replace this fragment with if there is any fragment on the container
            fragmentTransaction.commit(); // load fragment

        }

    }
}