package com.example.kamel.project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kamel.project.Model.VideoModel;
import com.example.kamel.project.R;
import com.example.kamel.project.VideoDescriptionActivity;
import com.example.kamel.project.VideoPlayerActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserVideoAdapter extends RecyclerView.Adapter<UserVideoAdapter.MyHolder>
{

    Context context ;
    ArrayList<VideoModel> videoModels ;

    public UserVideoAdapter(Context context, ArrayList<VideoModel> videoModels) {
        this.context = context;
        this.videoModels = videoModels;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_video,viewGroup,false) ;
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {
        final VideoModel videoModel = videoModels.get(i) ;
        final String image = videoModel.getUrlPhoto() ;
        myHolder.tvTitle.setText(videoModel.getTitle());
        String duration = formatDuration( Long.parseLong(videoModel.getDuration() ) );

        String timeStamp = videoModel.getDate() ;
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH) ;
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString() ;
        myHolder.tvDate.setText(dateTime);

        myHolder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class) ;
                intent.putExtra("url_video",videoModel.getUrlVideo()) ;
                context.startActivity(intent) ;
            }
        });

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black_24dp).networkPolicy(NetworkPolicy.OFFLINE).into(myHolder.ivThumbnail, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(image).into(myHolder.ivThumbnail);
                }
            });
        }catch (Exception e){

        }

        myHolder.tvDuration.setText(duration) ;

        myHolder.btnLookDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoDescriptionActivity.class) ;
                intent.putExtra("title",videoModel.getTitle()) ;
                intent.putExtra("date",videoModel.getDate()) ;
                intent.putExtra("desc",videoModel.getDescription()) ;
                intent.putExtra("duration",videoModel.getDuration()) ;
                intent.putExtra("url_photo",videoModel.getUrlPhoto()) ;
                intent.putExtra("url_video",videoModel.getUrlVideo()) ;
                intent.putExtra("id",videoModel.getId()) ;
                intent.putExtra("id_category",videoModel.getIdCategory()) ;
                context.startActivity(intent) ;
            }
        });
    }

    private String formatDuration(long timeInMillisec){
        long min = TimeUnit.MILLISECONDS.toMinutes(timeInMillisec) ;
        long sec =         TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec)) ;

        if (sec < 10 ){
            return String.format("%d:" + "0" + "%d",
                    min, sec
            );
        }else {
            return String.format("%d:%d",
                    min, sec
            );
        }

    }

    @Override
    public int getItemCount() {
        return videoModels.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public ImageView ivThumbnail ;
        public TextView tvDuration , tvTitle, tvDate ;
        public Button btnLookDescription ;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.ivThumbnail) ;
            tvDuration = itemView.findViewById(R.id.tvDuration) ;
            tvTitle = itemView.findViewById(R.id.tvTitle) ;
            tvDate = itemView.findViewById(R.id.tvDate) ;
            btnLookDescription = itemView.findViewById(R.id.btnLookDescription) ;
        }
    }
}
