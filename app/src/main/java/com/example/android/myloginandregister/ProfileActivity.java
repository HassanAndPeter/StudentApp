package com.example.android.myloginandregister;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.myloginandregister.Adapters.CourseAdapter;
import com.example.android.myloginandregister.Model.Course;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    List<String> courseIDs;
    List<Course> courses;
    CourseAdapter courseAdapter;
    TextView textViewWarning;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    LinearLayout linearLayoutAddNewCourse, layoutProgress;
    ImageView backArrow;
    TextView textViewProgramName,textViewName,textViewEmail,textViewStudentID;
     String selectedSubject="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        intiView();
        intiResources();
        setRecyclerView();
        getAllCourses();
        getUserInfo();
    }

    private void getUserInfo() {
        DatabaseReference rep1= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(user.getUid());
        // bellow method will trigger only once
        rep1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textViewProgramName.setText(snapshot.child("programName").getValue(String.class));
                textViewName.setText(snapshot.child("userName").getValue(String.class));
                textViewEmail.setText(snapshot.child("email").getValue(String.class));
                textViewStudentID.setText(snapshot.child("studentID").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setRecyclerView() {
        courseAdapter = new CourseAdapter(courses, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(courseAdapter);
        courseAdapter.setOnItemClickListener(new CourseAdapter.onItemClickListener() {
            @Override
            public void delete(int position) {
                reference.child(courseIDs.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showToast("Remove Successfully ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Please Try again");
                    }
                });
            }



            @Override
            public void attendance(int position) {

            }
        });
    }


    private void getAllCourses() {
        layoutProgress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courses.clear();
                courseIDs.clear();
                if(snapshot.getChildrenCount()<=0){
                    textViewWarning.setVisibility(View.VISIBLE);
                }else {
                    for(DataSnapshot data:snapshot.getChildren()){
                        int totalAttendance=0;
                        int present=0;
                        DataSnapshot snapshotAttendance=data.child("courseAttendance");
                        for(DataSnapshot snapshotDay:snapshotAttendance.getChildren()){
                            totalAttendance++;
                            if(snapshotDay.child("attendanceStatus").getValue(String.class).equals("present")){
                                present++;
                            }
                        }

                        courseIDs.add(data.getKey());
                        Course course=data.getValue(Course.class);
                        if(totalAttendance==0){
                            course.setTotalAttendancePercentage("none");
                        }else {
                            int percent=(100*present)/totalAttendance;
                            course.setTotalAttendancePercentage(String.valueOf(percent));
                        }
                        courses.add(course);
                    }
                    textViewWarning.setVisibility(View.GONE);
                }
                recyclerView.setVisibility(View.VISIBLE);
                layoutProgress.setVisibility(View.GONE);
                courseAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void intiResources() {
        courseIDs = new ArrayList<>();
        courses = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(user.getUid()).child("courses");
    }
    private void intiView() {
        textViewWarning = findViewById(R.id.textViewWarning);
        recyclerView = findViewById(R.id.recyclerView);
        textViewProgramName=findViewById(R.id.textViewProgramName);
        textViewName=findViewById(R.id.textViewName);
        textViewEmail=findViewById(R.id.textViewEmail);
        textViewStudentID=findViewById(R.id.textViewStudentID);
        linearLayoutAddNewCourse = findViewById(R.id.linearLayoutAddNewCourse);
        backArrow=findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linearLayoutAddNewCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewCourseDialog();
            }
        });
        layoutProgress = findViewById(R.id.layoutProgress);
    }

    // check this function in AttendanceActivity
    private void openAddNewCourseDialog() {
        selectedSubject="";
        DatabaseReference referenceCourse;
        referenceCourse=FirebaseDatabase.getInstance().getReference()
                .child("courses");

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dailog_pick_new_course);
        dialog.setCancelable(false);
        dialog.show();
        Button buttonCancel, buttonAdd;
        final LinearLayout progressLayout,mainLayout;

        /***  Create array of check box  */
        final CheckBox[] checkBox=new CheckBox[4];
        checkBox[0]=dialog.findViewById(R.id.checkBoxSubject1);
        checkBox[1]=dialog.findViewById(R.id.checkBoxSubject2);
        checkBox[2]=dialog.findViewById(R.id.checkBoxSubject3);
        checkBox[3]=dialog.findViewById(R.id.checkBoxSubject4);
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
        buttonAdd=dialog.findViewById(R.id.buttonAdd);
        progressLayout=dialog.findViewById(R.id.progressLayout);


        mainLayout=dialog.findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        referenceCourse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for(DataSnapshot course:snapshot.getChildren()){
                    checkBox[i].setText(course.child("name").getValue(String.class));
                    i++;
                }
                mainLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
            }
        });


        checkBox[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox[0].isChecked()){
                    checkBox[1].setChecked(false);
                    checkBox[2].setChecked(false);
                    checkBox[3].setChecked(false);
                }else{
                    checkBox[0].setChecked(true);
                }
                selectedSubject=checkBox[0].getText().toString();
            }
        });

        checkBox[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox[1].isChecked()){
                    checkBox[0].setChecked(false);
                    checkBox[2].setChecked(false);
                    checkBox[3].setChecked(false);
                }else{
                    checkBox[1].setChecked(true);
                }
                selectedSubject=checkBox[1].getText().toString();
            }
        });
        checkBox[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox[2].isChecked()){
                    checkBox[0].setChecked(false);
                    checkBox[1].setChecked(false);
                    checkBox[3].setChecked(false);
                }else{
                    checkBox[2].setChecked(true);
                }
                selectedSubject=checkBox[2].getText().toString();
            }
        });
        checkBox[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox[3].isChecked()){
                    checkBox[0].setChecked(false);
                    checkBox[1].setChecked(false);
                    checkBox[2].setChecked(false);
                }else{
                    checkBox[3].setChecked(true);
                }
                selectedSubject=checkBox[3].getText().toString();
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Course course=new Course();
                course.setCourseName(selectedSubject);
                if(course.getCourseName().equals("")){
                    showToast("Please Select Module");
                }else {
                    for(int i=0;i<courses.size();i++){
                        if(courses.get(i).getCourseName().equals(selectedSubject)){
                            showToast("This Course Already Picked");
                            return;
                        }
                    }

                    mainLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.VISIBLE);
                    reference.push().setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast("successfully Created Successfully");
                            mainLayout.setVisibility(View.VISIBLE);
                            progressLayout.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Please try again"+ "  Error"+e.getMessage());

                            mainLayout.setVisibility(View.VISIBLE);
                            progressLayout.setVisibility(View.GONE);}
                    });
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }
}