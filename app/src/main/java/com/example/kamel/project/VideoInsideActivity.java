package com.example.kamel.project;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.kamel.project.Adapter.UserVideoAdapter;
import com.example.kamel.project.Model.VideoModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VideoInsideActivity extends AppCompatActivity {

    RecyclerView recyclerView ;
    UserVideoAdapter videoAdapter ;
    ArrayList<VideoModel> videoModels ;

    String idIntent ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_inside);

        ActionBar actionBar = getSupportActionBar() ;
        actionBar.setTitle("Video") ;
      //  this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView) ;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() ) );

        idIntent = getIntent().getExtras().getString("id","");

        videoModels = new ArrayList<>() ;
        getVideo() ;

    }

    void getVideo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CategoryVideo").child(idIntent).child("List") ;
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videoModels.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren() ){
                    VideoModel videoModel = new VideoModel() ;
                    String title = ds.child("title").getValue(String.class) ;
                    String photo = ds.child("photo").getValue(String.class) ;
                    String desc = ds.child("desc").getValue(String.class) ;
                    String date = ds.child("date").getValue(String.class) ;
                    String id = ds.child("id").getValue(String.class) ;
                    String duration = ds.child("duration").getValue(String.class) ;
                    String video = ds.child("video").getValue(String.class) ;

                    videoModel.setTitle(title);
                    videoModel.setUrlPhoto(photo);
                    videoModel.setDescription(desc);
                    videoModel.setDate(date);
                    videoModel.setId(id);
                    videoModel.setDuration(duration);
                    videoModel.setUrlVideo(video);
                    videoModel.setIdCategory(idIntent) ;

                    videoModels.add(videoModel) ;
                }

                videoAdapter = new UserVideoAdapter(VideoInsideActivity.this,videoModels) ;
                videoAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(videoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
    }
}
