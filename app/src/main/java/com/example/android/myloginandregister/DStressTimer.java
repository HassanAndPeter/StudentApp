package com.example.android.myloginandregister;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class DStressTimer extends AppCompatActivity {
ImageView backArrow;
Button buttonBreathe;
ProgressBar progressBarCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_stress_timer);
        initView();
    }

    private void initView() {
        backArrow=findViewById(R.id.backArrow);
        buttonBreathe=findViewById(R.id.buttonBreathe);
        progressBarCount=findViewById(R.id.progressBarCount);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonBreathe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new CountDownTimer(2000, 1000) {
                    int index=0;
                    public void onTick(long millisUntilFinished) {
                        index++;
                        progressBarCount.setProgress((int)millisUntilFinished);
                    }

                    public void onFinish() {
                        index++;
                        progressBarCount.setProgress(0);

                    }
                }.start();

            }
        });
    }
}