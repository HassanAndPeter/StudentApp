package com.example.android.myloginandregister;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelpFulTips extends AppCompatActivity {
  ImageView backArrow;
  TextView textViewNumber,textViewHelpFulTipsPlaceHolder;
  List<String> randomNumbers=new ArrayList<>();
  Button buttClickForTip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_ful_tips);
        initList();
        initView();
    }

    private void initList() {
        for(int i=1;i<=10;i++){
            randomNumbers.add(String.valueOf(i));
        }
    }

    private void initView() {

        backArrow=findViewById(R.id.backArrow);
          backArrow.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  finish();
              }
          });
        //

        textViewNumber=findViewById(R.id.textViewNumber);
        textViewHelpFulTipsPlaceHolder=findViewById(R.id.textViewHelpFulTipsPlaceHolder);
        buttClickForTip=findViewById(R.id.buttClickForTip);

        buttClickForTip.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Collections.shuffle(randomNumbers);// shuffle Numbers
                textViewNumber.setText(randomNumbers.get(4));
                textViewHelpFulTipsPlaceHolder.setText(
                        getString(R.string.help_place_holder)
                        +"=" +randomNumbers.get(4)
                );


            }
        });
    }
}