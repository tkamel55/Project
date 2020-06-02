package com.example.kamel.project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kamel.project.Model.CategorieModel;
import com.example.kamel.project.Adapter.CategoryAdminAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdmin extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    private Dialog loadingDialog, categoryDialog;

    private CategoryAdmin activity;


    private FloatingActionButton AddCategory;

    private CircleImageView addImage;
    private EditText Categoryname;
    private Button addBtn;

    private RecyclerView recyclerView;
    private List<CategorieModel> list;
    private CategoryAdminAdapter adapter;
    private Uri image;
    private String downloadUrl;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_admin);

        AddCategory = findViewById(R.id.addCategory);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add Quiz");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ab.setSubtitle("This is Subtitle");


        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        setCategoryDialog();


        recyclerView = findViewById(R.id.rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        adapter = new CategoryAdminAdapter(list, new CategoryAdminAdapter.DeleteListener() {
            @Override
            public void onDelete(final String key, final int position) {

                new AlertDialog.Builder(CategoryAdmin.this,R.style.DefaultAlertDialogTheme).setTitle("Delete Category")
                        .setMessage("Are you sure , you want to delete this category").setPositiveButton("Delete"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.show();
                                ref.child("Categories").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            ref.child("SETS").child(list.get(position).getName()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        list.remove(position);
                                                        adapter.notifyDataSetChanged();
                                                    } else{
                                                        Toast.makeText(CategoryAdmin.this, "Failed to delete", Toast.LENGTH_SHORT).show();

                                                    }
                                                    loadingDialog.dismiss();

                                                }
                                            });


                                        } else {
                                            Toast.makeText(CategoryAdmin.this, "Failed to delete", Toast.LENGTH_SHORT).show();


                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert).show();


            }
        });
        recyclerView.setAdapter(adapter);

        AddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                categoryDialog.show();


            }
        });




        loadingDialog.show();
        ref.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    list.add(new CategorieModel(dataSnapshot1.child("name").getValue().toString(),
                            Integer.parseInt(dataSnapshot1.child("sets").getValue().toString()),
                            dataSnapshot1.child("url").getValue().toString(),
                            dataSnapshot1.getKey()
                    ));
                }

                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(CategoryAdmin.this, databaseError.getMessage(),
                        Toast.LENGTH_SHORT);
                loadingDialog.dismiss();
                finish();
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setCategoryDialog() {

        categoryDialog = new Dialog(this);
        categoryDialog.setContentView(R.layout.add_category_dialog);
        categoryDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.box_cornes));
        categoryDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        categoryDialog.setCancelable(true);

        addImage = categoryDialog.findViewById(R.id.image_view);
        Categoryname = categoryDialog.findViewById(R.id.categoryname);
        addBtn = categoryDialog.findViewById(R.id.addBtn);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 101);

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Categoryname.getText() == null) {
                    Categoryname.setError("Required");
                    return;
                }
                for(CategorieModel model : list) {
                    if( Categoryname.getText().toString().equals(model.getName())){
                        Categoryname.setError("Category name is already been used !");
                    }
                }
                if (image == null) {
                    Toast.makeText(CategoryAdmin.this, "Please select your image", Toast.LENGTH_SHORT).show();

                    return;
                }
                categoryDialog.dismiss();
                //uploading data
                uploadData();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {

            if (resultCode == RESULT_OK) {

                image = data.getData();
                addImage.setImageURI(image);

            }

        }


    }

    private void uploadData() {
        loadingDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference imageReference = storageReference.child("Categories").child(image.getLastPathSegment());

        UploadTask uploadTask = imageReference.putFile(image);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult().toString();
                            uploadCategoryName();

                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(CategoryAdmin.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    loadingDialog.dismiss();

                    Toast.makeText(CategoryAdmin.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void uploadCategoryName() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", Categoryname.getText().toString());
        map.put("sets", 0);
        map.put("url", downloadUrl);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("Categories").child("Categories" + (list.size() + 1)).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    list.add(new CategorieModel(Categoryname.getText().toString(), 0, downloadUrl, "Categories" + (list.size() + 1)));
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(CategoryAdmin.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
                loadingDialog.dismiss();
            }
        });

    }
}
