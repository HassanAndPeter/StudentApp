package com.example.android.myloginandregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class StudySelection extends AppCompatActivity {
  ImageView backArrow;// image view variable for back arrow on left top of the screen
  Button buttonFindCourse,// Button variable for find course check UI activity_study_selection.xml
          buttonAdmissionOffice;// Button variable for Admissions office check UI activity_study_selection.xml
    // once the the activity start loading onCreate() function will be called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//default save instance of this activity on stack
        setContentView(R.layout.activity_study_selection);// set UI activity_study_selection.xml of StudySelection.java
        initView(); //this function will connect UI components check initView() function bellow
    }

    // initView( function
    private void initView() {
        backArrow=findViewById(R.id.backArrow);// get back arrow from Ui
        buttonFindCourse=findViewById(R.id.buttonFindCourse);// get button find course from Ui
        buttonAdmissionOffice=findViewById(R.id.buttonAdmissionOffice);// get button Admissions office button from Ui

        // make click listener for buttonFindCourse when some on click on buttonFindCourse bellow onClick will be called
        buttonFindCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// click listener for buttonFindCourse
                //call LoadURL() function passing url=getString(R.string.find_course_link) will get url from string.xml file
                LoadURL(getString(R.string.find_course_link),getString(R.string.button_find_course));// check UrlLoader() function bellow

            }
        });

        // same to buttonFindCourse button just getting different url from string.xml
         buttonAdmissionOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadURL(getString(R.string.admission_office_link),getString(R.string.button_admission_office));
            }
        });

       // click listener for back arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// finish active and go back to previous activity
            }
        });
    }
//////  Load URL function
    private void LoadURL(String url, String actionType) { //argument url and action Type

        // create intent = intent used to start new activity
        Intent intent=new Intent(com.example.android.myloginandregister.StudySelection.this,UrlLoader.class);
        intent.putExtra("url",url);// put url to intent
        intent.putExtra("actionName",actionType);// put action name or action type to intent
        startActivity(intent); // start intent  will load target Activity
    }
}