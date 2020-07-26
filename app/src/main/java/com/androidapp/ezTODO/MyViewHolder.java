package com.androidapp.ezTODO;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public MyViewHolder(@NonNull View itemView){
        super(itemView);
        mView = itemView;
    }

    public void setTask(String task){
        TextView taskTextView = mView.findViewById(R.id.task_tv);
        taskTextView.setText(task);
    }

    public void setDescription(String desc){
        TextView descTextView = mView.findViewById(R.id.description_tv);
        descTextView.setText(desc);
    }

    public void setDate(String date){
        TextView dateTextview = mView.findViewById(R.id.datetv);
        dateTextview.setText(date);
    }
}
