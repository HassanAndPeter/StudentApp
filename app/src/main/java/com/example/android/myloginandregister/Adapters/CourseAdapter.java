package com.example.android.myloginandregister.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.myloginandregister.Model.Course;
import com.example.android.myloginandregister.R;

import java.util.List;

// working same like attendance Adapter
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CustomViewHolder> {

    List<Course> courses;
    Context context;
    private  onItemClickListener mListener;
      public  interface onItemClickListener{

          void  delete(int position);
          void   attendance(int position);

        }
     public  void setOnItemClickListener(onItemClickListener listener){
          mListener=listener;
     }
     public static class  CustomViewHolder extends RecyclerView.ViewHolder{
      ImageView imageViewDelete;
      TextView textViewCourseName,textViewPercentNumber;
      ProgressBar progressBarRed,progressBarGreen;
      Button buttonAttendance;
        public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
            imageViewDelete=itemView.findViewById(R.id.imageViewDelete);
            buttonAttendance=itemView.findViewById(R.id.buttonAttendance);
            textViewCourseName=itemView.findViewById(R.id.textViewCourseName);
            textViewPercentNumber=itemView.findViewById(R.id.textViewPercent);
            progressBarRed=itemView.findViewById(R.id.progressRed);
            progressBarGreen=itemView.findViewById(R.id.progressGreen);

           imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.delete(position);
                        }
                    }
                }
            });
            buttonAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.attendance(position);
                        }
                    }
                }
            });



        }
    }
    public CourseAdapter(List<Course> courses, Context context) {
        this.courses = courses;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
            return R.layout.receycler_view_course_item;
    }
    @Override
    public int getItemCount() {
        return  courses.size();
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false),mListener);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

          holder.textViewCourseName.setText(courses.get(position).getCourseName());
         if(!courses.get(position).getTotalAttendancePercentage().equals("none")) {
             holder.textViewPercentNumber.setVisibility(View.VISIBLE);
             holder.textViewPercentNumber.setText(courses.get(position).getTotalAttendancePercentage());
             int coursePercent = Integer.parseInt(courses.get(position).getTotalAttendancePercentage());
             holder.progressBarGreen.setProgress(coursePercent);
             holder.progressBarRed.setProgress(coursePercent);
             if (coursePercent < 70) {
                 holder.progressBarGreen.setVisibility(View.GONE);
                 holder.progressBarRed.setVisibility(View.VISIBLE);
                 holder.textViewPercentNumber.setTextColor(holder.textViewCourseName.getContext().getColor(R.color.warning_color));
             } else {
                 holder.progressBarRed.setVisibility(View.GONE);
                 holder.progressBarGreen.setVisibility(View.VISIBLE);
                 holder.textViewPercentNumber.setTextColor(holder.textViewCourseName.getContext().getColor(R.color.green_color));
             }
         }else {
             holder.progressBarRed.setVisibility(View.GONE);
             holder.progressBarGreen.setVisibility(View.GONE);
             holder.textViewPercentNumber.setVisibility(View.GONE);

         }

      }

}
