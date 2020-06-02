package com.example.kamel.project.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamel.project.AddVideoInsideCategoryActivity;
import com.example.kamel.project.Model.CategoryVideo;
import com.example.kamel.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryVideoAdapter extends RecyclerView.Adapter<CategoryVideoAdapter.MyHolder>
{
    Context context ;
    ArrayList<CategoryVideo> categoryVideos ;

    public CategoryVideoAdapter(Context context, ArrayList<CategoryVideo> categoryVideos) {
        this.context = context;
        this.categoryVideos = categoryVideos;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category,viewGroup,false) ;
        return new CategoryVideoAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        final CategoryVideo categoryVideo = categoryVideos.get(i) ;
        myHolder.tvCategory.setText(categoryVideo.getName());

        myHolder.linearLayoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddVideoInsideCategoryActivity.class) ;
                intent.putExtra("id",categoryVideo.getId()) ;
                context.startActivity(intent) ;
            }
        });

        myHolder.tvNumber.setText(categoryVideo.getnVideo() + " videos") ;

        myHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namingCategory(categoryVideo.getName(),categoryVideo.getId()) ;
            }
        });

        myHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertShow(categoryVideo.getId());
            }
        });

        //Toast.makeText(context,categoryVideo.getName(),Toast.LENGTH_SHORT).show();
    }

    void namingCategory(String lastName,final String idCategory){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context) ;
        mBuilder.setTitle("Edit Category's Name") ;
        mBuilder.setMessage("Name : ") ;
        mBuilder.setIcon(R.mipmap.ic_launcher_round) ;

        final EditText input = new EditText(context);
        input.setText(lastName) ;

        mBuilder.setView(input);
        mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getActivity(),"Cancel",Toast.LENGTH_SHORT).show();
            }
        }) ;

        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sInput = input.getText().toString() ;
                addCategory(sInput,idCategory) ;
                //Toast.makeText(context,sInput,Toast.LENGTH_SHORT).show();
            }
        }) ;

        AlertDialog mDialog = mBuilder.create() ;
        mDialog.show();
    }
    void addCategory(String categoryName,String idCategory){
        final ProgressDialog progressDialog = new ProgressDialog(context) ;
        progressDialog.setMessage("Change Category's Name");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryVideo") ;
        String idPos = idCategory ;

        HashMap<String, Object> hashMap = new HashMap<>() ;
        hashMap.put("id",idPos) ;
        hashMap.put("name",categoryName) ;

        reference.child(idPos).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                }
                else
                {
                    Toast.makeText(context,"Terjadi Kesalahan",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }) ;
    }

    public void AlertShow(final String idCategory){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context) ;
        mBuilder.setMessage("Are you sure to delete this category?") ;
        mBuilder.setCancelable(false) ;
        mBuilder.setIcon(R.mipmap.ic_launcher_round) ;

        mBuilder.setPositiveButton("Yes"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        delete(idCategory);
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
    void delete(final String idCategory){
        final ProgressDialog progressDialog = new ProgressDialog(context) ;
        progressDialog.setMessage("Delete");
        progressDialog.setCancelable(false);
        progressDialog.show();


        DatabaseReference referenceCategory = FirebaseDatabase.getInstance().getReference("CategoryVideo").child(idCategory).child("List") ;
        referenceCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String id = snapshot.child("id").getValue(String.class) ;
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("videos").child(idCategory).child("url_photo").child(id + ".png" ) ;
                    storageReference.delete() ;
                    StorageReference storageReference2 = FirebaseStorage.getInstance().getReference("videos").child(idCategory).child("url_video").child(id + ".mp4" ) ;
                    storageReference2.delete() ;
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryVideo").child(idCategory) ;
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                        }

                    }
                }) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return categoryVideos.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public LinearLayout linearLayoutCategory ;
        public TextView tvCategory , tvNumber ;
        public Button btnEdit , btnDelete ;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutCategory = itemView.findViewById(R.id.linearLayoutCategory) ;
            tvCategory = itemView.findViewById(R.id.tvCategory) ;
            tvNumber = itemView.findViewById(R.id.tvNumber) ;

            btnEdit = itemView.findViewById(R.id.btnEdit) ;
            btnDelete = itemView.findViewById(R.id.btnDelete) ;
        }
    }
}
