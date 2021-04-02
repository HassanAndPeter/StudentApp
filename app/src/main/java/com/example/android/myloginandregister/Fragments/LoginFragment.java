package com.example.android.myloginandregister.Fragments;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.android.myloginandregister.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Objects;


public class LoginFragment extends Fragment {
    View rootView;
    EditText editTextEmail,editTextPassword;
    TextView textViewForgotPassword,textViewSignUp;
    Button buttonLogin;
    FragmentTransaction fragmentTransaction;
    FirebaseAuth auth;
    FirebaseUser user;
    MiscHelper miscHelper=new MiscHelper();
    Dialog dialogProgress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_login, container, false);
        initView();

        return rootView;

    }

    private void initView() {
        editTextEmail=rootView.findViewById(R.id.editTextEmail);
        editTextPassword=rootView.findViewById(R.id.editTextPassword);
        textViewForgotPassword=rootView.findViewById(R.id.textViewForgotPassword);
        textViewSignUp=rootView.findViewById(R.id.textViewSignUp);
        buttonLogin=rootView.findViewById(R.id.buttonLogin);



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();

            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpFragment();
            }
        });

    }

    private void openSignUpFragment() {
        fragmentTransaction= Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new SignUpFragment());
        fragmentTransaction.commit();
    }

    private void resetPassword() {

        // check MainActivity.java for alretdialog
        final EditText resetPasswordEmail=new EditText(getContext());
        resetPasswordEmail.setPadding(20,20,20,30);
        resetPasswordEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0,0 , 0);
        resetPasswordEmail.setCompoundDrawablePadding(20);

        final AlertDialog.Builder dialogResetPassword=new AlertDialog.Builder(getContext());
        dialogResetPassword.setTitle("Reset Password ?");
        dialogResetPassword.setMessage("Enter your email to received reset link.");
        dialogResetPassword.setView(resetPasswordEmail);

        dialogResetPassword.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String email=resetPasswordEmail.getText().toString();
                if(email.equals("")){
                    showToast("Please Enter Email");
                }else if(!miscHelper.isEmailValid(email)){
                    showToast("your email is not valid ");
                }else {

                    // this firebase mothod will send reset link to email
                    auth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            alertDialog("Reset Link sent to your email");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            alertDialog("Error: "+e.getMessage());

                        }
                    });
                }
            }
        });

        dialogResetPassword.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogResetPassword.create().show();

    }
    private void doLogin() {
        String email=editTextEmail.getText().toString();
        final String password=editTextPassword.getText().toString();
        if(email.equals("")){
            alertDialog("Please enter email");
        }else if(password.equals("")){
            alertDialog("Please provide password");
        }else if(!miscHelper.isEmailValid(email)){
            alertDialog("Please Provide correct email");
        }else {
            openDialog();
            auth= FirebaseAuth.getInstance();



    // above everything same to Registration activity

    //signInWithEmailAndPassword(email,password) function check user credentials
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    user=auth.getCurrentUser();//if successful get current user
                    assert user != null;
                    if(!user.isEmailVerified()){ // check user verified there email
                        alertDialog("Please Verify your email");
                        auth.signOut();// make sign out again
                    }else {// if everything okay

                        // start main activity using intent
                        Objects.requireNonNull(getActivity()).finish();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }


                    dialogProgress.dismiss();// remove dialog
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogProgress.dismiss();
                    alertDialog("Error : "+e.getMessage());
                }
            });
        }
    }
    private void alertDialog(String message){
        new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    private void showToast(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
    }
    public void openDialog(){
        dialogProgress=new Dialog(Objects.requireNonNull(getActivity()));
        Objects.requireNonNull(dialogProgress.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogProgress.setContentView(R.layout.dialog_progress);
        dialogProgress.setCancelable(false);
        dialogProgress.show();
    }


}