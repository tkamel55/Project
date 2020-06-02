package com.example.kamel.project;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.kamel.project.Adapter.AllUsersListAdapter;
import com.example.kamel.project.Model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUserListActivity extends AppCompatActivity
{
    private RecyclerView allusers_rv;
    private ArrayList<Student> arrayList;
    private AllUsersListAdapter adapter_users;
    DatabaseReference dbReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user_list);

        getSupportActionBar().setTitle("ALL USERS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference().child("Students");

        allusers_rv = findViewById(R.id.all_users_RV);
        allusers_rv.hasFixedSize();
        allusers_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        arrayList = new ArrayList<>();

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                arrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Student model = ds.getValue(Student.class);
                    arrayList.add(model);
                }
                adapter_users = new AllUsersListAdapter(getApplicationContext(), arrayList);
                allusers_rv.setAdapter(adapter_users);
                adapter_users.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
