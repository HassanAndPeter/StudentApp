package com.example.android.myloginandregister.Model;

public class Course {
    String courseName;
    String TotalAttendancePercentage;

    public Course() {
    }

    public Course(String courseName, String totalAttendancePercentage) {
        this.courseName = courseName;
        TotalAttendancePercentage = totalAttendancePercentage;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTotalAttendancePercentage() {
        return TotalAttendancePercentage;
    }

    public void setTotalAttendancePercentage(String totalAttendancePercentage) {
        TotalAttendancePercentage = totalAttendancePercentage;
    }
}
