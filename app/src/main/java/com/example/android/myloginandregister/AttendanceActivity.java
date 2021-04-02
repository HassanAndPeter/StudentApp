package com.example.android.myloginandregister;

import android.app.Dialog;
import android.content.Intent;
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

public class AttendanceActivity extends AppCompatActivity {

    List<String> courseIDs;// list used to store ids of courses
    List<Course> courses;// list using custom class Course.java custom object to store all courese
    CourseAdapter courseAdapter;// course adapter
    TextView textViewWarning;// red textView

    // bellow all variables same to AddAttendanceActivity.java class
    RecyclerView recyclerView;//
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    LinearLayout linearLayoutAddNewCourse, layoutProgress;
    ImageView backArrow;
    TextView textViewProgramName;
    String  selectedSubject="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        intiView();
        intiResources();
        setRecyclerView();
        getAllCourses();
        getProgramName();
    }

    //get All Ui components
    private void intiView() {
        textViewWarning = findViewById(R.id.textViewWarning);
        recyclerView = findViewById(R.id.recyclerView);
        textViewProgramName=findViewById(R.id.textViewProgramName);
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
                openAddPickCourseDialog();

            }
        });
        layoutProgress = findViewById(R.id.layoutProgress);
    }
    //bellow method will get program name from firebase 
    private void getProgramName() {
        DatabaseReference rep1= FirebaseDatabase.getInstance().getReference()//firebase root 
                .child("users")// inside root go down to users
                .child(user.getUid());// inside users go down to specific user using user id 
        // bellow method will trigger only once
        //call the reference 
        rep1.addListenerForSingleValueEvent(new ValueEventListener() {
           
           // when call successfull bellow mwthod will be execute 
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            
                // get course name from reference and set it on textView Program name 
                textViewProgramName.setText(snapshot.child("programName").getValue(String.class));
            }
            
            // bellow method will be called when request cancel
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              // show error here 
            }
        });
    }
    private void intiResources() {
        courseIDs = new ArrayList<>();// initialize course ids list
        courses = new ArrayList<>();// initialize courses list
        auth = FirebaseAuth.getInstance();// get firebase auth object
        user = auth.getCurrentUser();//get current user from auth
        reference = FirebaseDatabase.getInstance().getReference()//firebase root
                .child("users")//in root move down to users
                .child(user.getUid())//in users move down to specific user using user id
                .child("courses");//in user id move down to courses
    }
    
    // set up recycler view
    private void setRecyclerView() {
        
        // create course adapter with courses list
        courseAdapter = new CourseAdapter(courses, this);
        
        //setup recycler view to show item in vertical form 
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        
        //set adapter on recyeler view 
        recyclerView.setAdapter(courseAdapter);
        
        //courseAdapter item click listener there are two item clickable one is delete and one is add attendance 
        courseAdapter.setOnItemClickListener(new CourseAdapter.onItemClickListener() {
            @Override
            public void delete(int position) {
                // courseIDs.get(position)= get course id from courseIDs list
                //move down in reference go to child specific course id 
                // and remove it 
                
                reference.child(courseIDs.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {// operation Success listener 

                        showToast("Remove Successfully ");// show message to user 
                    }
                }).addOnFailureListener( //add failure listener 
                        new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Please Try again");// show message to user 
                    }
                });
            }
            //if user click on attendance
            @Override
            public void attendance(int position) {
                //create intent for AddAttendanceActivity.java class
                Intent intent=new Intent(AttendanceActivity.this,AddAttendanceActivity.class);

                // add course id to intent
                intent.putExtra("courseID",courseIDs.get(position));

                //add courseName to intent
                intent.putExtra("courseName",courses.get(position).getCourseName());

                // start activity
                startActivity(intent);
            }
        });
    }



    private void getAllCourses() {
        layoutProgress.setVisibility(View.VISIBLE);//visible progress layout
        recyclerView.setVisibility(View.GONE);//hide main layout
        linearLayoutAddNewCourse.setVisibility(View.GONE);// hide add new course layout

         //get all courses from reference
        // using addValueEventListener this will method will be automatically trigger when change occur in a child
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                linearLayoutAddNewCourse.setVisibility(View.VISIBLE); // visible addNewCourse layout
                courses.clear();//clear courses list
                courseIDs.clear();// clear courseIDs List
                if(snapshot.getChildrenCount()<=0){// if no course found
                    textViewWarning.setVisibility(View.VISIBLE);// visible no item found layout

                }else {

                    // traverse all the courses
                    for(DataSnapshot data:snapshot.getChildren()){
                        // data= single course
                         int totalAttendance=0; // totalAttendance variable for calculation of attendance
                         int present=0; //present variable for calculation of % attendance

                        //inside move to course attendance
                        DataSnapshot snapshotAttendance=data.child("courseAttendance");

                        //travers all attendance
                         for(DataSnapshot snapshotDay:snapshotAttendance.getChildren()){
                             totalAttendance++;//count total attendance

                             // if the attendance is present
                             if(snapshotDay.child("attendanceStatus").getValue(String.class).equals("present")){
                                 present++;// increment present attendance variable
                             }
                         }

                        courseIDs.add(data.getKey());// get course id
                        Course course=data.getValue(Course.class);// get course object
                        if(totalAttendance==0){// if there is no attendance
                            course.setTotalAttendancePercentage("none");// set attendance to none
                        }else {
                            int percent=(100*present)/totalAttendance;// calculate percent attendance
                            course.setTotalAttendancePercentage(String.valueOf(percent));// set percent attendance to object
                        }
                        courses.add(course);// add course to courses list
                    }
                    textViewWarning.setVisibility(View.GONE);// hide red color text view
                }
                recyclerView.setVisibility(View.VISIBLE);//visible recycler view
                layoutProgress.setVisibility(View.GONE);//hide progress bar
                courseAdapter.notifyDataSetChanged();// refresh adapter


            }
             //when database request cancel
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                linearLayoutAddNewCourse.setVisibility(View.VISIBLE);//visible addNewCourse layout
            }
        });


    }

/**This is new development  **/
    private void openAddPickCourseDialog() {

        selectedSubject="";// used for selected object
        DatabaseReference referenceCourse;
        referenceCourse=FirebaseDatabase.getInstance().getReference()//firebase root
                .child("courses");//move down to courses

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dailog_pick_new_course);
        dialog.setCancelable(false);
        dialog.show();
        // above lines will create dialog and open it
        Button buttonCancel, buttonAdd;
        final LinearLayout progressLayout,mainLayout;

       /***  Create array of check box  */
        final CheckBox[] checkBox=new CheckBox[4];

        //get all checkbox from Ui
        checkBox[0]=dialog.findViewById(R.id.checkBoxSubject1);
        checkBox[1]=dialog.findViewById(R.id.checkBoxSubject2);
        checkBox[2]=dialog.findViewById(R.id.checkBoxSubject3);
        checkBox[3]=dialog.findViewById(R.id.checkBoxSubject4);
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
        buttonAdd=dialog.findViewById(R.id.buttonAdd);
        progressLayout=dialog.findViewById(R.id.progressLayout);
        mainLayout=dialog.findViewById(R.id.mainLayout);
        mainLayout.setVisibility(View.GONE); //hide main Layout
        progressLayout.setVisibility(View.VISIBLE);// visible progress layout

        //make listener for courses
        referenceCourse.addListenerForSingleValueEvent(new ValueEventListener() {
            // bellow method will return all modules
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   int i=0;
                for(DataSnapshot course:snapshot.getChildren()){// get courses one by one
                    checkBox[i].setText(course.child("name").getValue(String.class));// get course name and assign it to check box
                    i++;
                }
                mainLayout.setVisibility(View.VISIBLE);// visible main layout
                progressLayout.setVisibility(View.GONE);//hide progress layout

            }
           //when request cancel
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mainLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
            }
        });

         // subject 1 checkbox click listener
        checkBox[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox[0].isChecked()){  //if checkbox 0 marked
                    checkBox[1].setChecked(false);// make check box 1 unMark
                    checkBox[2].setChecked(false);//make check box 2 unMark
                    checkBox[3].setChecked(false);//make check box 3 unMark
                }else{// if check box 1 unMark
                    checkBox[0].setChecked(true);// mark check box 0
                }
                selectedSubject=checkBox[0].getText().toString();  // get subject name from checkbox 0
            }
        });

        //same working like check 0
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
        //same working like check 0
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
        //same working like check 0
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

       // button add click listener
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Course course=new Course();// create new course object
                course.setCourseName(selectedSubject); // set course name
                if(course.getCourseName().equals("")){// if course name empty
                    showToast("Please Select Module");// show message
                }else {// if everything okay

                    // first check the course already is already added or not
                    for(int i=0;i<courses.size();i++){
                        if(courses.get(i).getCourseName().equals(selectedSubject)){
                            showToast("This Course Already Picked");//show message
                            return;
                        }
                    }

                    mainLayout.setVisibility(View.GONE);//hide main layout
                    progressLayout.setVisibility(View.VISIBLE);//visible progress layout

                    // push course to reference . push() firebase function will create id for course
                    reference.push().setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {//success listener
                            showToast("successfully Created Successfully");//show message
                            mainLayout.setVisibility(View.VISIBLE);
                            progressLayout.setVisibility(View.GONE);
                            dialog.dismiss();// hide dialog
                        }
                    }).addOnFailureListener(new OnFailureListener() {//trace error
                        @Override
                        public void onFailure(@NonNull Exception e) {
                         showToast("Please try again"+ "  Error"+e.getMessage());

                            mainLayout.setVisibility(View.VISIBLE);
                            progressLayout.setVisibility(View.GONE);}
                    });
                }
            }
        });
        //button cancel click listener
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//close dialog
            }
        });
    }

    // show message or toast to user
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }
}