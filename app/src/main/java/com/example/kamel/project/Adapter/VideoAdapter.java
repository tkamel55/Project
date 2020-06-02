package com.example.kamel.project.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kamel.project.EditVideoActivity;
import com.example.kamel.project.Model.VideoModel;
import com.example.kamel.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyHolder>
{

    Context context ;
    ArrayList<VideoModel> videoModels ;

    public VideoAdapter(Context context, ArrayList<VideoModel> videoModels) {
        this.context = context;
        this.videoModels = videoModels;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video,viewGroup,false) ;
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {
        final VideoModel videoModel = videoModels.get(i) ;
        final String image = videoModel.getUrlPhoto() ;
        String duration = formatDuration( Long.parseLong(videoModel.getDuration() ) );

        myHolder.tvTitle.setText(videoModel.getTitle());

        String timeStamp = videoModel.getDate() ;
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH) ;
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString() ;
        myHolder.tvDate.setText(dateTime);

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

        myHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditVideoActivity.class) ;

                intent.putExtra("title",videoModel.getTitle()) ;
                intent.putExtra("date",videoModel.getDate()) ;
                intent.putExtra("desc",videoModel.getDescription()) ;
                intent.putExtra("duration",videoModel.getDuration()) ;
                intent.putExtra("url_photo",videoModel.getUrlPhoto()) ;
                intent.putExtra("url_video",videoModel.getUrlVideo()) ;
                intent.putExtra("id",videoModel.getId() ) ;
                intent.putExtra("id_category",videoModel.getIdCategory()) ;

                context.startActivity(intent) ;
            }
        });

        myHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertShow(videoModel.getId(),videoModel.getIdCategory() ) ;
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

    public void AlertShow(final String id,final String idCategory){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context) ;
        mBuilder.setMessage("Are you sure to delete this video?") ;
        mBuilder.setCancelable(false) ;
        mBuilder.setIcon(R.mipmap.ic_launcher_round) ;

        mBuilder.setPositiveButton("Yes"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        delete(id,idCategory);
                    }
                })
                .setNegativeButton("No"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }) ;

        AlertDialog mDialog = mBuilder.create() ;
        mDialog.show();

    }

    void delete(final String id,final String idCategory){
        final ProgressDialog progressDialog = new ProgressDialog(context) ;
        progressDialog.setMessage("Delete");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("videos").child(idCategory).child("url_photo").child(id + ".png" ) ;
        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    StorageReference storageReference2 = FirebaseStorage.getInstance().getReference("videos").child(idCategory).child("url_video").child(id + ".mp4" ) ;
                    storageReference2.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryVideo").child(idCategory).child("List").child(id)  ;
                                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                        }

                                    }
                                }) ;
                            }
                        }
                    });

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return videoModels.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public ImageView ivThumbnail ;
        public TextView tvDuration , tvTitle, tvDate ;
        public Button btnEdit , btnDelete ;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.ivThumbnail) ;
            tvDuration = itemView.findViewById(R.id.tvDuration) ;
            tvTitle = itemView.findViewById(R.id.tvTitle) ;
            tvDate = itemView.findViewById(R.id.tvDate) ;

            btnEdit = itemView.findViewById(R.id.btnEdit) ;
            btnDelete = itemView.findViewById(R.id.btnDelete) ;
        }
    }
}
