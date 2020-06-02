package com.example.kamel.project;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;




public class AdminRegister extends AppCompatActivity implements View.OnClickListener, SignupManager.SignUpFeedbackListener{

    private EditText etEmailAddress, etPass, etCfPass;
    private Button ibtnSignUp;
    private SignupManager signupManager;
    private AdminRegister activity;
    private TextView GologinUp;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        GologinUp = findViewById(R.id.admin_login_page);

        GologinUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // starting background task to update product
                Intent intent=new Intent(getApplicationContext(),AdminLogin.class);
                startActivity(intent);
            }
        });

        initUI();
    }

    // init ui elements
    private void initUI() {
        activity = this;
        signupManager = new SignupManager(activity, true, this);


        etEmailAddress = findViewById(R.id.etCreEmail);
        etPass = findViewById(R.id.etCrePass);
        etCfPass = findViewById(R.id.confirmPass);
        ibtnSignUp = findViewById(R.id.admin_reg);



        ibtnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.admin_reg:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        signupManager.signUpUser( etEmailAddress.getText().toString(),
                etPass.getText().toString(), etCfPass.getText().toString());
    }

    private void startLoginActivity() {
        startActivity(new Intent(AdminRegister.this, AdminPage.class));
        finish();
    }

    @Override
    public void signUpSuccess() {
        Toast.makeText(activity, "Sign up success.Now you can login", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLoginActivity();
            }
        }, 2000);
    }

    @Override
    public void signUpFailed() {
        Toast.makeText(activity, "Error Occurred while processing the request", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void emailError() {
        etEmailAddress.setError("Please provide a valid email");

    }

    @Override
    public void passwordError() {
        etCfPass.setError("Please provide a valid password with minimum >5 character");

    }


    @Override
    public void validationError() {
        Toast.makeText(activity, "Fillup the form properly", Toast.LENGTH_SHORT).show();
    }
}
