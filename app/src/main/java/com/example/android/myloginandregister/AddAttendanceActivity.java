package com.example.android.myloginandregister;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.myloginandregister.Adapters.AttendanceAdapter;
import com.example.android.myloginandregister.Helper.DatePickerFragment;
import com.example.android.myloginandregister.Model.Attendance;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AddAttendanceActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener{
    List<String> attendanceIDs; // this list will store all attendance ids
    List<Attendance> attendances;// this list will store all attendance list using custom class Attendance.jav
    AttendanceAdapter attendanceAdapter;// variable attendance adapter
    TextView textViewWarning;// red color textView we will show this  when there is no attendance
    RecyclerView recyclerView;// recycler View to show all attendance
    FirebaseAuth auth;// firebase auth object
    FirebaseUser user;//firebase user object
    DatabaseReference reference;// database reference
    LinearLayout linearLayoutAddAttendance,// layout add attendance
            layoutProgress;// loading layout
    EditText editTextDate;// edit text date
    TextView textViewCourseName;// textView for course name
    ImageView backArrow;// back arrow
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance);
        intiView();// this function used to connect all component of UI
        intiResources();// this function connect firebase
        setRecyclerView();// this function will setUp recycler view
        getAllAttendance();//this function used to get all attendance
    }
    private void setRecyclerView() {
        //initialize attendance adapter
        attendanceAdapter = new AttendanceAdapter(attendances, this);

        //set recycler view to show items vertically
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //set Adapter on recyclerView
        recyclerView.setAdapter(attendanceAdapter);

        // attendance adapter click listener
        attendanceAdapter.setOnItemClickListener(new AttendanceAdapter.onItemClickListener() {
            // listener for delete
            @Override
            public void delete(int position) {
                /** when someOne clicked on delete
                 * adapter will send position of that item to this
                 * delete function
                 *  */
                //  get attendanceID from attendanceIDs List
                //reference pointing to all attendance of user check initResource() function
                // so inside reference go to attendance id
                // using remove value function to remove the attendance
                reference.child(attendanceIDs.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { // onSuccess listener this function will be called when remove successful
                        showToast("Remove Successfully");// call show Toast function to display message to user
                    }
                }).addOnFailureListener(new OnFailureListener() {// when operation not successful
                    @Override
                    public void onFailure(@NonNull Exception e) {// onFailure called
                        showToast("Please Try again");// show message to user
                    }
                });
            }
            // click listener for Edit
            @Override
            public void edit(int position) {
                //call openEditDialog
                openEditDialog(attendances.get(position),position);
            }
        });
    }
    // this method will get all attendance from firebase for user
    private void getAllAttendance() {
        layoutProgress.setVisibility(View.VISIBLE);// visible Loading dialog
        recyclerView.setVisibility(View.GONE);//hide recyclerView

        /**reference.addValueEventListener will be trigger again
         * and again when some thing change to reference
         *
         * **/
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendanceIDs.clear();// clear attendance ids List
                attendances.clear();//clear attendance List
                if(snapshot.getChildrenCount()<=0){// if there is no child in the reference
                    textViewWarning.setVisibility(View.VISIBLE); // visible red textView ="No Item found"
                }else {// if reference contains child= attendance list
                    for(DataSnapshot data:snapshot.getChildren()){//traverse all attendance
                        attendanceIDs.add(data.getKey());// store attendance id
                        Attendance attendance=data.getValue(Attendance.class);// get attendance object
                        attendances.add(attendance);// add attendance object to attendance List
                    }
                    textViewWarning.setVisibility(View.GONE);// hide red color textView
                }
                recyclerView.setVisibility(View.VISIBLE);// make recycler view Visible
                layoutProgress.setVisibility(View.GONE);//hide progress bar

                Collections.reverse(attendanceIDs); // reverse all ids
                Collections.reverse(attendances);// reverse all attendance so new attendance will be on top
                attendanceAdapter.notifyDataSetChanged();// refresh adapter
            }
            //  when request cancel
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void intiResources() {
        Intent intent=getIntent();// get intent
        //get course id from intent this ids need for pull,add and update attendance to course
        String courseID=intent.getStringExtra("courseID");

        //get course name from intent
        String courseName=intent.getStringExtra("courseName");

        //set course name on intent
        textViewCourseName.setText(courseName);

        //initialize attendance list
        attendances = new ArrayList<>();

        //initialize attendanceIDs list
        attendanceIDs = new ArrayList<>();

        //get firebase auth object
        auth = FirebaseAuth.getInstance();

        //get current user from auth object
        user = auth.getCurrentUser();

        //create reference to course
        reference = FirebaseDatabase.getInstance().getReference()//database root
                .child("users")//in root move down to users
                .child(user.getUid())// in users move down to user ID
                .child("courses")// in user ID move down to courses
                .child(courseID)// in courses move down to specific course using course id
                .child("courseAttendance");// in course id move down to course attendance
    }

    //connect all Ui component
    private void intiView() {
        textViewWarning = findViewById(R.id.textViewWarning);
        recyclerView = findViewById(R.id.recyclerView);
        backArrow=findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        linearLayoutAddAttendance = findViewById(R.id.linearLayoutAddNewAttendance);
        textViewCourseName=findViewById(R.id.textViewCourseName);
        linearLayoutAddAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNewAttendanceDialog();
            }
        });
        layoutProgress = findViewById(R.id.layoutProgress);
    }
    //openEditDialog accept attendance object
    private void openEditDialog(Attendance attendance, final int position) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);// full screen dialog
        dialog.setContentView(R.layout.dialog_add_attendance);// load UI on dialog
        dialog.setCancelable(false);// make dialog not cancelable with backPress
        dialog.show();// show dialog

        // UI components
        final LinearLayout progressLayout,mainLayout,linearLayoutDate;
        final CheckBox checkBoxPresent,checkBoxAbsent;
        Button buttonAdd,buttonCancel;
        progressLayout=dialog.findViewById(R.id.layoutProgress);
        linearLayoutDate=dialog.findViewById(R.id.linearLayoutDate);
        mainLayout=dialog.findViewById(R.id.mainLayout);
        editTextDate=dialog.findViewById(R.id.editTextDate);
        editTextDate.setText(attendance.getAttendanceDate());
        checkBoxPresent=dialog.findViewById(R.id.checkBoxPresent);
        checkBoxAbsent=dialog.findViewById(R.id.checkBoxAbsent);
        buttonAdd=dialog.findViewById(R.id.buttonAdd);
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
        /***Above lines will connect all UI components   ****/

        // user present make present check box true
        if(attendance.getAttendanceStatus().equals("present")){
            checkBoxPresent.setChecked(true);//set checked pragmatically
            checkBoxAbsent.setChecked(false);
        }else {// if not present make absent check box true
            checkBoxAbsent.setChecked(true);
            checkBoxPresent.setChecked(false);
        }

        buttonAdd.setText("Update"); // write text on add button=update

        //make listener for checkBoxAbsent
        checkBoxAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            // this method onclick() will be called when some one click on checkBoxAbsent()
            public void onClick(View v) {
                if(checkBoxAbsent.isChecked()){ // if check box marked
                    checkBoxPresent.setChecked(false);//unMark present check box
                }else {// if present check box unMarked
                    checkBoxAbsent.setChecked(true);// make present check box marked
                }
            }
        });

        // working same like checkBoxAbsent();
        checkBoxPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxPresent.isChecked()){
                    checkBoxAbsent.setChecked(false);
                }else {
                    checkBoxPresent.setChecked(true);
                }
            }
        });


        // date click listener
        linearLayoutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open Date packer Fragment check DatePackerFragment.java inside helper
                DialogFragment datePicker = new DatePickerFragment();// create instance of date picker fragment
                datePicker.show(getSupportFragmentManager(), "date picker");// show datePickerFragment
            }
        });

        // button cancel click listener
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();// close dialog
            }
        });

        // button add click listener
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Attendance attendance=new Attendance();// create attendance object

                // get attendance data from Ui and write it on attendance object
                attendance.setAttendanceDate(editTextDate.getText().toString());

               // set attendance status
                if(checkBoxAbsent.isChecked()){ // if absent check
                    attendance.setAttendanceStatus("absent");// set absent
                }else if(checkBoxPresent.isChecked()){// if present checked
                    attendance.setAttendanceStatus("present");//set present
                }

                // if no date selected
                if(attendance.getAttendanceDate().equals("")){
                    showToast("Please Select Date"); // show message to user
                }else if(attendance.getAttendanceStatus().equals("")){ //if no status selected
                    showToast("Please Select Status");// show message to user
                }else {// elese everything fine
                    progressLayout.setVisibility(View.VISIBLE);// make progress visible
                    mainLayout.setVisibility(View.GONE);// hide main layout

                    // get attendance id from attendanceIDs List using =attendanceIDs.get(position))

                    //inside reference move to attendance ID and upload attendance object to that is
                    reference.child(attendanceIDs.get(position)).setValue(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {// when data successfully uploaded
                            progressLayout.setVisibility(View.GONE);// hide progress layout
                            mainLayout.setVisibility(View.VISIBLE);//visible main layout
                            dialog.dismiss();// close dialog
                        }
                    }).addOnFailureListener(new OnFailureListener() {// when data not uploaded due to some reasons
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Please Try again");// show message
                            progressLayout.setVisibility(View.GONE);// hide progress bar
                            mainLayout.setVisibility(View.VISIBLE);// visible main layout
                        }
                    });
                }
            }
        });
    }

// bellow function same working like openEditDialog() above
    private void openAddNewAttendanceDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_add_attendance);
        dialog.setCancelable(false);
        dialog.show();

        final LinearLayout progressLayout,mainLayout,linearLayoutDate;

        final CheckBox checkBoxPresent,checkBoxAbsent;
        Button buttonAdd,buttonCancel;
        progressLayout=dialog.findViewById(R.id.layoutProgress);
        linearLayoutDate=dialog.findViewById(R.id.linearLayoutDate);
        mainLayout=dialog.findViewById(R.id.mainLayout);
        editTextDate=dialog.findViewById(R.id.editTextDate);
        editTextDate.setText(getCurrentDate());
        checkBoxPresent=dialog.findViewById(R.id.checkBoxPresent);
        checkBoxAbsent=dialog.findViewById(R.id.checkBoxAbsent);
        checkBoxPresent.setChecked(true);
        buttonAdd=dialog.findViewById(R.id.buttonAdd);
        checkBoxAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxAbsent.isChecked()){
                    checkBoxPresent.setChecked(false);
                }else {
                    checkBoxAbsent.setChecked(true);
                }
            }
        });
        checkBoxPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxPresent.isChecked()){
                    checkBoxAbsent.setChecked(false);
                }else {
                    checkBoxPresent.setChecked(true);
                }
            }
        });
        buttonCancel=dialog.findViewById(R.id.buttonCancel);
   linearLayoutDate.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           DialogFragment datePicker = new DatePickerFragment();
           datePicker.show(getSupportFragmentManager(), "date picker");
       }
     });
buttonCancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        dialog.dismiss();
    }
});


         buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Attendance attendance=new Attendance();
            attendance.setAttendanceDate(editTextDate.getText().toString());
            if(checkBoxAbsent.isChecked()){
                attendance.setAttendanceStatus("absent");
            }else if(checkBoxPresent.isChecked()){
                attendance.setAttendanceStatus("present");
            }

            if(attendance.getAttendanceDate().equals("")){
                showToast("Please Select Date");
            }else if(attendance.getAttendanceStatus().equals("")){
                showToast("Please Select Status");
            }else {
                boolean insertionFlag = true;
                for (int i = 0; i < attendances.size(); i++) {
                    if (attendances.get(i).getAttendanceDate().equals(attendance.getAttendanceDate()))
                        insertionFlag = false;
                }
          if(insertionFlag){
                progressLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
                reference.push().setValue(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressLayout.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Please Try again");
                        progressLayout.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                    }
                });
            }else {
                    showToast("this "+editTextDate.getText().toString()+" Attendance Already taken");
               }
            }
         }
        });

    }

    // this function will show message to user
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    // when user select date for attendance when adding or updating  attendance
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();// create calender object
        c.set(Calendar.YEAR, year);//set year
        c.set(Calendar.MONTH, month);//set month
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);//set day

        // get date from calender object in String
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        // set string date on editTextDate
        editTextDate.setText(currentDateString);
    }

    // current date function
    private String getCurrentDate() {
        Date today = new Date();// create date object
        Calendar cal = Calendar.getInstance();// get calender instance
        cal.setTime(today);// get current date
        Date date=cal.getTime();// get time from date

        // get time from date object into String format
        String formatDate= DateFormat.getDateInstance(DateFormat.FULL).format(date);

        // return date
        return  formatDate;
    }
}