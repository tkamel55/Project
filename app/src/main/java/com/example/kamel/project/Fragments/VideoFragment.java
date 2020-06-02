package com.example.kamel.project.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kamel.project.Adapter.UserCategoryVideoAdapter;
import com.example.kamel.project.Model.CategoryVideo;
import com.example.kamel.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class VideoFragment extends Fragment {

    RecyclerView recyclerView ;

    UserCategoryVideoAdapter categoryVideoAdapter ;
    ArrayList<CategoryVideo> categoryVideos ;


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = view.findViewById(R.id.recyclerView) ;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        categoryVideos = new ArrayList<>() ;
        getCategory();

        return view ;
    }

    private void getCategory() {

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

                categoryVideoAdapter = new UserCategoryVideoAdapter(getActivity(),categoryVideos) ;
                categoryVideoAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(categoryVideoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

    }
}
