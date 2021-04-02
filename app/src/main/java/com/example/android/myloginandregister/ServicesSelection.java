package com.example.android.myloginandregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ServicesSelection extends AppCompatActivity {

 // this activity working on the same to StudySelection.java activity
 ImageView backArrow;
 Button buttonStudy,buttonCounsellingServices,buttonTutoringCenter,buttonHealthCenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_selection);

        backArrow=findViewById(R.id.backArrow);
        buttonStudy=findViewById(R.id.buttonStudy);
        buttonCounsellingServices=findViewById(R.id.buttonCounsellingServices);
        buttonTutoringCenter=findViewById(R.id.buttonTutoringCenter);
        buttonHealthCenter=findViewById(R.id.buttonHealthCenter);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(com.example.android.myloginandregister.ServicesSelection.this,StudySelection.class));
            }
        });

        buttonCounsellingServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadURL(getString(R.string.counseling_services_link),"Counseling Services");

            }
        });

        buttonTutoringCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadURL(getString(R.string.tutoring_center_link),"Tutoring Center");

            }
        });

        buttonHealthCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadURL(getString(R.string.health_center_link),"Health Center");
            }
        });
    }

    private void LoadURL(String url, String actionType) {
        Intent intent=new Intent(com.example.android.myloginandregister.ServicesSelection.this,UrlLoader.class);
        intent.putExtra("url",url);
        intent.putExtra("actionName",actionType);
        startActivity(intent);
    }
}