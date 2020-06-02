package com.example.kamel.project;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AdminPage extends AppCompatActivity {

     ImageView adminlogout;
     LinearLayout quizPage , VideoPage, Students, Mail  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      // ActionBar ab = getSupportActionBar();
    //    ab.setTitle("Admin Page");
     //   ab.setSubtitle("Choose any of the option below");

        setContentView(R.layout.activity_admin_page);
        adminlogout = findViewById(R.id.LogoutAdmin);
        quizPage = findViewById(R.id.quiz);
        VideoPage = findViewById(R.id.video);
        Students = findViewById(R.id.students);
        Mail = findViewById(R.id.message);


        VideoPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),VideoActivity.class);
                startActivity(intent);

            }
        });

        Mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SendingMailActivity.class);
                startActivity(intent);

            }
        });


        quizPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(),CategoryAdmin.class);
                startActivity(intent);

            }
        });

        Students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AllUserListActivity.class);
                startActivity(intent);
            }
        });


        adminlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferenceValue.clearLoggedInuserData(getApplicationContext());
                // SharedPreferenceValue.clearLastScore(activity);
                if (SharedPreferenceValue.getLoggedinUser(getApplicationContext()) == -1) {
                    Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AdminLogin.class));
                    finish();
                }

            }
        });

    }
}
