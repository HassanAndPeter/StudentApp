package com.example.android.myloginandregister.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.android.myloginandregister.Helper.MiscHelper;
import com.example.android.myloginandregister.MainActivity;
import com.example.android.myloginandregister.Model.User;
import com.example.android.myloginandregister.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Objects;


public class SignUpFragment extends Fragment {

    View rootView;
    EditText editTextUserName,editTextEmail,editTextPassword,editTextStudentID
            ,editTextProgramName;
    TextView textViewLogin;
    Button buttonRegister;
    FragmentTransaction fragmentTransaction;
    MiscHelper miscHelper=new MiscHelper();// check miscHelper.jav it is used for email validation
    FirebaseAuth auth;// firebase auth object
    FirebaseUser user;//firebase user object
    DatabaseReference reference;// firebase reference
    Dialog dialogProgress; // progress dialog


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference()// firebase database root
                .child("users");//in root move down users

    }


    //Loads Ui on fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_sign_up, container, false);
        initView();// connect Ui component
        return  rootView;
    }

    // connect all Ui components
    private void initView() {
        editTextUserName=rootView.findViewById(R.id.editTextUserName);
        editTextEmail=rootView.findViewById(R.id.editTextEmail);
        editTextPassword=rootView.findViewById(R.id.editTextPassword);
        textViewLogin=rootView.findViewById(R.id.textViewLogin);
        buttonRegister=rootView.findViewById(R.id.buttonRegister);
        editTextProgramName=rootView.findViewById(R.id.editTextProgramName);
        editTextStudentID=rootView.findViewById(R.id.editTextStudentID);
         // when user press register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegistration();// call do registration function
            }
        });

        // when user click on textView ("have account login")
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSignInFragment();// load signUp fragment
            }
        });
    }
    private void doRegistration() {

        // get user details from UI to User Object
        User user=new User(editTextEmail.getText().toString()
                ,editTextUserName.getText().toString()
                ,editTextProgramName.getText().toString()
                ,editTextStudentID.getText().toString());

         // get password from UI
        String password=editTextPassword.getText().toString();

        // bellow all if else check that the user provide all the details
        if(user.getUserName().equals("")){
            alertDialog("Please enter user name ");
        }else if(user.getEmail().equals("")){
            alertDialog("You must need to enter email");
        }else if(!miscHelper.isEmailValid(user.getEmail())){// check if email is invalid
            alertDialog("your email is not valid");
        }else if(password.equals("")){
            alertDialog("Please enter password ");
        }else if(password.length()<=5) {// check if password length less then 6
            alertDialog("Password length must be greater then 5 character");
        }else if(user.getStudentID().equals("")) {
            alertDialog("Please Enter your ID");
        }else if(user.getProgramName().equals("")){
            alertDialog("Please Enter program name");
        }else {// if everything okay
            createAuth(user,password); // create auth function
        }
    }
    private void createAuth(final User userObject,String password) {
        openDialog(); // call openDialog() function to open progress dialog

       /**
        * createUserWithEmailAndPassword(email,password) takes email and passord
        * and create user on firebase using its email and password
        * */
        auth.createUserWithEmailAndPassword(userObject.getEmail(),password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {// if registration successful
                user=auth.getCurrentUser();// get current user from auth
                assert user != null;

                // upload user object to firebase
                reference.child(user.getUid()).setValue(userObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Login Successful");
                        dialogProgress.dismiss();// remove dialog

                        // send email to user for verification
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                alertDialog("Please go to your email and verify your email");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog("Error : "+e.getMessage());
                            }
                        });
                        // if user email is already verified , it is not possible but just in case
                        if(user.isEmailVerified()) {
                            // start main activity using intent
                            Objects.requireNonNull(getActivity()).finish();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogProgress.dismiss(); // remove dialog
                        alertDialog("Error :"+ e.getMessage());//  show error message
                    }
                });
            }
            // this method will return error if error occurs for example user with this email already exist etc
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogProgress.dismiss();
                alertDialog("Error:" + e.getMessage());
            }
        });
    }
   // loading SignUp fragment check Registration.java for same coding
    private void loadSignInFragment() {
        fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());
        fragmentTransaction.commit();
    }

    //show message to user
    private void showToast(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
    }
    //show dialog to user check same coding of dialog on MainActivity.java of alertDialog
    private void alertDialog(String message){
        new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    // open custom progress dialog
    public void openDialog(){
        dialogProgress=new Dialog(Objects.requireNonNull(getActivity()));
        Objects.requireNonNull(dialogProgress.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogProgress.setContentView(R.layout.dialog_progress);
        dialogProgress.setCancelable(false);
        dialogProgress.show();
    }
}
