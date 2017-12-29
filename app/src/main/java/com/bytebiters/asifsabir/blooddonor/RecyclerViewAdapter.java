package com.bytebiters.asifsabir.blooddonor;

/**
 * Created by asifsabir on 12/19/17.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<BloodReq> MainImageUploadInfoList;

    public RecyclerViewAdapter(Context context, List<BloodReq> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BloodReq studentDetails = MainImageUploadInfoList.get(position);

        holder.tvRequesterName.setText(studentDetails.name);

        holder.tvBloodGroup.setText(studentDetails.bloodGroup);

        holder.tvView.setText(studentDetails.view);

        holder.tvTimeStamp.setText(studentDetails.timeStamp);
        holder.tvLocation.setText(studentDetails.location);
        holder.tvPhone.setText(studentDetails.phone);


    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvRequesterName,tvBloodGroup,tvView,tvTimeStamp,tvLocation,tvPhone;

        public ViewHolder(View itemView) {

            super(itemView);

            tvRequesterName = (TextView) itemView.findViewById(R.id.tv_requester_name);

            tvBloodGroup = (TextView) itemView.findViewById(R.id.tv_blood_group);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);

            tvView = (TextView) itemView.findViewById(R.id.tv_view);

            tvTimeStamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);


        }
    }
}