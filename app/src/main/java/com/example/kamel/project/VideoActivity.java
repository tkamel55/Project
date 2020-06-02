package com.example.kamel.project;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kamel.project.Adapter.CategoryVideoAdapter;
import com.example.kamel.project.Model.CategoryVideo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoActivity extends AppCompatActivity {

    RecyclerView recyclerView ;

    CategoryVideoAdapter categoryVideoAdapter ;
    ArrayList<CategoryVideo> categoryVideos ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar() ;
        actionBar.setTitle("Video") ;

        recyclerView = findViewById(R.id.recyclerView) ;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(VideoActivity.this));

        categoryVideos = new ArrayList<>() ;
        getCategory();

        Button btnAdd = findViewById(R.id.btnAdd) ;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namingCategory();
            }
        });

    }

  public void getCategory(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CategoryVideo") ;
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryVideos.clear();

                for(DataSnapshot ds : dataSnapshot.getChildren() ){
                    CategoryVideo categoryVideo = new CategoryVideo() ;
                    String id = ds.child("id").getValue(String.class) ;
                    String name = ds.child("name").getValue(String.class) ;
                    int nVideo = (int) ds.child("List").getChildrenCount() ;

                    categoryVideo.setId(id) ;
                    categoryVideo.setName(name) ;
                    categoryVideo.setnVideo(nVideo);

                    categoryVideos.add(categoryVideo) ;
                }

                categoryVideoAdapter = new CategoryVideoAdapter(VideoActivity.this,categoryVideos) ;
                categoryVideoAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(categoryVideoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
    }
    public void namingCategory(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(VideoActivity.this);
        mBuilder.setTitle("Add Video Category") ;
        mBuilder.setMessage("Name : ") ;
        mBuilder.setIcon(R.mipmap.ic_launcher_round) ;

        final EditText input = new EditText(VideoActivity.this);

        mBuilder.setView(input);
        mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }) ;

        mBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sInput = input.getText().toString() ;
                addCategory(sInput) ;
            }
        }) ;

        AlertDialog mDialog = mBuilder.create() ;
        mDialog.show();
    }
    public void addCategory(String categoryName){
        final ProgressDialog progressDialog = new ProgressDialog(VideoActivity.this) ;
        progressDialog.setMessage("Add Category");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryVideo") ;
        String idPos= reference.push().getKey() ;

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("id",idPos) ;
        hashMap.put("name",categoryName) ;

        reference.child(idPos).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                }
                else
                {
                    Toast.makeText(VideoActivity.this,"There is an error",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }) ;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
