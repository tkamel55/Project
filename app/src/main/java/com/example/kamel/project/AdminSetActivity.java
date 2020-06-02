package com.example.kamel.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kamel.project.Adapter.AdminGridAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminSetActivity extends AppCompatActivity {

    GridView gridView;
    Dialog loadingDialog;
    AdminGridAdapter adapter;
    String categoryName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_set);


        gridView = findViewById(R.id.gridview);
        categoryName=getIntent().getStringExtra("title");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryName);


        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        adapter=new AdminGridAdapter(getIntent().getIntExtra("sets", 0), getIntent().getStringExtra("title"), new AdminGridAdapter.GrideListener() {
            @Override
            public void addSet() {
                loadingDialog.show();
                FirebaseDatabase database=FirebaseDatabase.getInstance();
                database.getReference().child("Categories").child(getIntent().getStringExtra("key")).child("sets").setValue(getIntent().getIntExtra("sets", 0)+1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    adapter.sets=adapter.sets+1;
                                    adapter.notifyDataSetChanged();
                                }else
                                {
                                    Toast.makeText(AdminSetActivity.this,"failed action",Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                }

                                loadingDialog.dismiss();
                            }
                        });
            }

            @Override
            public void onLongClick(final int setNo) {
                new AlertDialog.Builder(AdminSetActivity.this,R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Delete Question")
                        .setMessage("Are you sure to delete this Set")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("SETS").child(categoryName).child("questions").orderByChild("setNo").equalTo(setNo)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                                                {
                                                    String id=dataSnapshot1.getKey();
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("SETS").child(categoryName).child("questions").child(id).removeValue();

                                                }
                                                adapter.sets--;
                                                loadingDialog.dismiss();
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                loadingDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"something wrong",Toast.LENGTH_LONG).show();

                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No",null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        gridView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

