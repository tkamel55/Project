package com.example.kamel.project;


import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdminLogin extends AppCompatActivity implements View.OnClickListener, LoginManager.LoginFeedbackListener{
    private EditText etCreEmail;
    private EditText etCrePass;
    private Button btnLogin;
    private LoginManager loginManager;
    private AdminLogin activity;

    private TextView StdAccount;

    private TextView GosignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        GosignUp = findViewById(R.id.regpage);

        StdAccount = findViewById(R.id.Studenpage);

        StdAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });


        GosignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // starting background task to update product
                Intent intent=new Intent(getApplicationContext(),AdminRegister.class);
                startActivity(intent);
            }
        });


        initUI();
    }

    private void initUI() {
        activity = this;
        etCreEmail = findViewById(R.id.etCreEmail);
        etCrePass = findViewById(R.id.etCrePass);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        loginManager = new LoginManager(activity, true, this);
        checkAlreadyLoginOrNot();
    }

    private void checkAlreadyLoginOrNot() {
        if (SharedPreferenceValue.getLoggedinUser(activity)!=-1){
            startDashboard();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_login:
                performLogin();
                break;
        }
    }

    private void performLogin() {
        loginManager.loginUser(etCreEmail.getText().toString(),
                etCrePass.getText().toString());
    }


    @Override
    public void getLoggedinUser(User user) {
        if (user != null) {
            SharedPreferenceValue.clearLoggedInuserData(activity);
            SharedPreferenceValue.setLoggedInUser(activity, user.getUserID());
            startDashboard();
        }
    }

    private void startDashboard() {
        startActivity(new Intent(AdminLogin.this, AdminPage.class));
        finish();
    }

    @Override
    public void noUserFound() {
        Toast.makeText(activity, "Wrong Credential.Login failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void emailError() {
        etCreEmail.setError(getString(R.string.email_required_text));
    }

    @Override
    public void passwordError() {
        etCrePass.setError("Please provide valid password that minimum 6 character");
    }
}
