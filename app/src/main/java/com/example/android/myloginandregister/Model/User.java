package com.example.android.myloginandregister.Model;

public class User {
    String email;
    String userName;
    String programName;
    String studentID;

    public User() {

    }

    public User(String email, String userName, String programName, String studentID) {
        this.email = email;
        this.userName = userName;
        this.programName=programName;
        this.studentID=studentID;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
