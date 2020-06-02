package com.example.kamel.project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamel.project.Model.BaseUtil;
import com.example.kamel.project.Model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Register extends AppCompatActivity {

    static int PReqCode = 1;
    static int REQUESCODE = 1;
    ImageView Profilepic;
    Uri pickImageuri;
    ProgressDialog progressDialog;
    private Button regBtn;
    private EditText userEmail, Password, Confirmpass, username;

    private TextView Account;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);

        Account = findViewById(R.id.loginPage);

        userEmail = findViewById(R.id.Email);
        Password = findViewById(R.id.password1);
        Confirmpass = findViewById(R.id.password2);


        regBtn = findViewById(R.id.btn_reg);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");


        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String email = userEmail.getText().toString();
                final String password = Password.getText().toString();
                final String password2 = Confirmpass.getText().toString();
                final String name = username.getText().toString();


                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || password2.isEmpty() || !password.equals(password2)) {

                    showMessage("Please fill in the information correctly");


                } else {

                    CreateAccount(email, name, password);
                }

            }
        });

        Profilepic = findViewById(R.id.icon2);

        Profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();


                } else {
                    openGallery();
                }


            }
        });


    }

    private void CreateAccount(final String email, final String name, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();

                            Student user = new Student(

                                    email,
                                    name

                            );

                            user.setUser_key(BaseUtil.getFirebaseAuth().getCurrentUser().getUid());



                            FirebaseDatabase.getInstance().getReference("Students")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                       // Toast.makeText(Register.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });


                            showMessage("Account Created");

                            if (pickImageuri != null) {

                                updateInfo(name, pickImageuri, mAuth.getCurrentUser());
                            } else {

                                progressDialog.dismiss();

                                updateInfoWithoutPic(name, mAuth.getCurrentUser());
                            }
                        } else {
                            progressDialog.dismiss();

                            showMessage("Account Not Created" + task.getException().getMessage());
                        }
                    }
                });

    }

    private void updateInfo(final String name, Uri pickImageuri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickImageuri.getLastPathSegment());

        imageFilePath.putFile(pickImageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profielUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profielUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();

                                            showMessage("Account Registered ");
                                            updateUI();


                                        }

                                    }
                                });

                    }
                });

            }
        });
    }

    private void updateInfoWithoutPic(final String name, final FirebaseUser currentUser) {

        progressDialog.show();


        UserProfileChangeRequest profielUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        currentUser.updateProfile(profielUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();


                            showMessage("Account Registered ");
                            updateUI();


                        }

                    }
                });


    }

    private void updateUI() {
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {


        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            //user to pic the image successfully
            // and get the uri details

            pickImageuri = data.getData();

            Profilepic.setImageURI(pickImageuri);


        }

    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Register.this, "Please accept for required permisson", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else {
            openGallery();
        }


    }
}
