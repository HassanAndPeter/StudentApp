package com.example.android.myloginandregister.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android.myloginandregister.Model.Attendance;
import com.example.android.myloginandregister.R;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<com.example.android.myloginandregister.Adapters.AttendanceAdapter.CustomViewHolder> {
    List<Attendance> attendances; //attendance List
    Context context;// application context
    private  onItemClickListener mListener; // adapter item click listener interface
      public  interface onItemClickListener{
          void  delete(int position);// edit function
          void   edit(int position);//delete function
        }

     public  void setOnItemClickListener(onItemClickListener listener){//item click listener initialization
          mListener=listener;
     }

     //this class used to hold custom View
     public static class  CustomViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDate// textView to show date
                ,textViewStatus;// textView to show status
        Button buttonDelete// button delete
                ,buttonEdit;//button edit

         // custom view holder constructor
         public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);

            //connect all UI components
            textViewDate=itemView.findViewById(R.id.textViewDate);
            textViewStatus=itemView.findViewById(R.id.textViewStatus);
            buttonDelete=itemView.findViewById(R.id.buttonDelete);
            buttonEdit=itemView.findViewById(R.id.buttonEdit);

            // click listener for delete button
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){ // check if listener not null
                        int position=getAdapterPosition();// get adapter position
                        if(position!= RecyclerView.NO_POSITION){//if position exist on adapter
                            listener.delete(position);// trigger delete function with item position
                        }
                    }
                }
            });

            // same like delete button
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.edit(position);// call edit function with item position
                        }
                    }
                }
            });
        }
    }

    // adapter constructor
    public AttendanceAdapter(List<Attendance> attendances, Context context) {
        this.attendances = attendances;// get all attendance
        this.context = context;//get context
    }

    // inflate view on adapter
    @Override
    public int getItemViewType(int position) {
            return R.layout.recycler_view_attendance_item;//check recycler_view_attendance_item.xml
    }
    @Override
    public int getItemCount() {
        return  attendances.size();// this will return attendance size
    }

    // this method not in use
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false),mListener);
    }

    //write date on Ui
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {


          //write date on textView
     holder.textViewDate.setText(attendances.get(position).getAttendanceDate());

     //write status on textView
     holder.textViewStatus.setText(attendances.get(position).getAttendanceStatus());

     // status is present make status color green
     if(attendances.get(position).getAttendanceStatus().equals("present")){
         holder.textViewStatus.setTextColor(holder.textViewStatus.getContext().getColor(R.color.green_color));

    //if status is absent make status red
     }else if(attendances.get(position).getAttendanceStatus().equals("absent")) {
         holder.textViewStatus.setTextColor(holder.textViewStatus.getContext().getColor(R.color.warning_color));
     }
      }
}
