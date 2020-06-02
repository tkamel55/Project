package com.example.kamel.project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kamel.project.Model.CategoryVideo;
import com.example.kamel.project.R;
import com.example.kamel.project.VideoInsideActivity;

import java.util.ArrayList;

public class UserCategoryVideoAdapter extends RecyclerView.Adapter<UserCategoryVideoAdapter.MyHolder>
{
    Context context ;
    ArrayList<CategoryVideo> categoryVideos ;

    public UserCategoryVideoAdapter(Context context, ArrayList<CategoryVideo> categoryVideos) {
        this.context = context;
        this.categoryVideos = categoryVideos;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_categories,viewGroup,false) ;
        return new UserCategoryVideoAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        final CategoryVideo categoryVideo = categoryVideos.get(i) ;
        myHolder.tvCategory.setText(categoryVideo.getName());

        myHolder.linearLayoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoInsideActivity.class) ;
                intent.putExtra("id",categoryVideo.getId()) ;
                context.startActivity(intent) ;
            }
        });

        myHolder.tvNumber.setText(categoryVideo.getnVideo() + " videos") ;


    }



    @Override
    public int getItemCount() {
        return categoryVideos.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public LinearLayout linearLayoutCategory ;
        public TextView tvCategory , tvNumber ;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutCategory = itemView.findViewById(R.id.linearLayoutCategory) ;
            tvCategory = itemView.findViewById(R.id.tvCategory) ;
            tvNumber = itemView.findViewById(R.id.tvNumber) ;
        }
    }
}
