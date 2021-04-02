package com.example.android.myloginandregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{


    DrawerLayout drawerLayout;
    TextView textName,textEmail,textViewName;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference userReference;
    LinearLayout linearLayoutTimeTable,linearLayoutAttendance
            ,linearLayoutStudentServices,linearLayoutRegistration
            ,linearLayoutStudy,linearLayoutHelp,linearLayoutTimer
            ,linearLayoutSignOut;
    /***Above all variables deceleration   ***/

    @Override
    /**onCreate() function this function will execute when activity start loading**/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//save Activty on stack
        setContentView(R.layout.activity_main);//Load UI
        initView();//call initView() function
        initSideNavigation();//call initSideNavigation() function
        initFirebase();//call initFirebase() function
        getUserInfo();//call getUserInfo() function
    }

    /***initView() Connect all Component of UI and stored the ids of component in variables   **/
    private void initView() {
        //get linearLayoutTimeTable from Ui
        linearLayoutTimeTable=findViewById(R.id.linearLayoutTimeTable);

        //make listener for linearLayoutTimeTable
        linearLayoutTimeTable.setOnClickListener(this);


        //get linearLayoutAttendance from Ui
        linearLayoutAttendance=findViewById(R.id.linearLayoutAttendance);

        //make listener for linearLayoutAttendance
        linearLayoutAttendance.setOnClickListener(this);

        // bellow all are same to above


        linearLayoutStudentServices=findViewById(R.id.linearLayoutStudentServices);
        linearLayoutStudentServices.setOnClickListener(this);

        linearLayoutRegistration=findViewById(R.id.linearLayoutRegistration);
        linearLayoutRegistration.setOnClickListener(this);

        linearLayoutStudy=findViewById(R.id.linearLayoutStudy);
        linearLayoutStudy.setOnClickListener(this);

        linearLayoutHelp=findViewById(R.id.linearLayoutHelp);
        linearLayoutHelp.setOnClickListener(this);

        linearLayoutTimer=findViewById(R.id.linearLayoutTimer);
        linearLayoutTimer.setOnClickListener(this);

        linearLayoutSignOut=findViewById(R.id.linearLayoutSignOut);
        linearLayoutSignOut.setOnClickListener(this);
        textViewName=findViewById(R.id.textViewName);
    }
    // side menu implementation
    private void initSideNavigation() {
        // get toolBar=the top line or top bar on the main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);// get tool bar from Ui screen
        setSupportActionBar(toolbar); // set toolbar

        // get drawer Layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // create toggle for drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle); /*** set drawer listener is a toggle
         *when some one clicked on drawer layout 3 small lines
         * so click will toggle the side navigation if the side navigation open close it, or  if close open it
         * **/
        toggle.syncState(); // syncState() for toggle
       // navigation view for drawerLayout
        navigationView = (NavigationView) findViewById(R.id.nav_view);//get navigation view from Ui
        navigationView.setNavigationItemSelectedListener(this);// click listener for item the items are profile and signOut in this case

        // get Navigation header
        View header = navigationView.getHeaderView(0);

        // on Navigation header there are two textView one for name and one for email


        // connect textViewName
        textName =  header.findViewById(R.id.textViewName);
       // connect textViewEmail
        textEmail =  header.findViewById(R.id.textViewPassword);
    }

    // when some one click on side navigation item bellow method will called
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {// after click any item close side navigation
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if(item.getItemId()==R.id.nav_profile){ // if some one click on Profile
           // start profile activity through intent
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }

        // if some one click on logout
        if(item.getItemId()==R.id.nav_logout){
            logout();// call logout function
            return false; //  don't change color of logout
        }else {
            return true;// if any other click change the color
        }
    }

    // logout function
    private void logout() {
        new AlertDialog.Builder(this)// create alert dialog
                .setMessage("Are you to Sign out from Application?")//set message on alert dialog
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {// set negative button "No" on dialog and click listener for "NO" button
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//click listener for "NO" button
                        dialog.dismiss();// close dialog
                    }
                })       // set positive button "Yes" on dialog
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override//
                    public void onClick(DialogInterface dialog, int which) {
                        if(firebaseUser!=null){  // if user is not null mean the login is made
                            firebaseAuth.signOut();// signOUt user
                            finish();// close mainActivity

                            // jump to registration Activity
                            startActivity(new Intent(MainActivity.this,Registration.class));
                        }
                    }
                })
                .show();// show dialog
    }

    // init firebase resource
    private void initFirebase() {
        firebaseAuth=FirebaseAuth.getInstance(); //get auth object
        firebaseUser=firebaseAuth.getCurrentUser();// get current user
        userReference= FirebaseDatabase.getInstance().getReference() // this will connect the top root of firebase datebase
                .child("users")// get child "users"
                .child(firebaseUser.getUid());// move down in users to current user
    }

    // get user details from firebase
    private void getUserInfo() {

        //call the reference using addListenerForSingleValueEvent this method called only once
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
           // onDateChange function will call when call complete and return user data in snapshot
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               //get user name and put name on side Navigation textViewName
                textName.setText(snapshot.child("userName").getValue(String.class));

                //get user name and put name on main Screen textViewName
                textViewName.setText(snapshot.child("userName").getValue(String.class));

                //get user email and put email on side Navigation textViewEmail
                textEmail.setText(snapshot.child("email").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // show error when request cancel
            }
        });
    }

    // main screen items clicks handler
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // when some one Time Table
            case R.id.linearLayoutTimeTable:
                // call LoadURL and pass url and action type
                LoadURL(getString(R.string.time_table_link),getString(R.string.time_table));
                break;
                //when some one click on attendance
            case R.id.linearLayoutAttendance:
                // start attendance activity using intent
                startActivity(new Intent(MainActivity.this,AttendanceActivity.class));
                break;
                //when someone click on student services
            case R.id.linearLayoutStudentServices:
                //start serviceSelectionActivity using intent
                startActivity(new Intent(MainActivity.this,ServicesSelection.class));
                break;
                //when click on registration activity
            case R.id.linearLayoutRegistration:
                //call LoadUrl() function and pass url and action name
                LoadURL(getString(R.string.payment_link),"Fee Service");
                break;


        }
    }

    // load Url function argument= url and actionType
    private void LoadURL(String url, String actionType) {
       // create intent
        Intent intent=new Intent(MainActivity.this,UrlLoader.class);

        //put url in intent
        intent.putExtra("url",url);


        // put actionType
        intent.putExtra("actionName",actionType);

        //start activity
        startActivity(intent);
    }
}