package com.example.kamel.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//implementing login and out system
public class MainActivity extends AppCompatActivity {

    TextView textView;

    private EditText userMail , userPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private Intent home;
    ProgressDialog pd;
    TextView Forgetpass;
    TextView Adminlog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userMail =findViewById(R.id.Email);
        userPassword = findViewById(R.id.password2);

        btnLogin = findViewById(R.id.btn_login);


        Forgetpass = findViewById(R.id.forgot_password);
        Adminlog = findViewById(R.id.admin);


        mAuth = FirebaseAuth.getInstance();
        home = new Intent(this,HomeActivity.class);




        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()) {

                    showMessage("Please fill in the fields correctly");

                }
                else {
                    signIn(mail,password);
                }

            }
        });

        pd= new ProgressDialog(this);



        textView = (TextView)findViewById(R.id.regaccount);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =  new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });


        Adminlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =  new Intent(MainActivity.this, AdminLogin.class);
                startActivity(intent);
            }
        });

        Forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });




    }

    private void showRecoverPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText userMail = new EditText(this);
        userMail.setHint("Email");
        userMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        userMail.setMinEms(16);

        linearLayout.addView(userMail);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = userMail.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    private void beginRecovery(String email) {

        pd.setMessage("Sending Email...");

        pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                pd.dismiss();
                if (task.isSuccessful()) {

                    Toast.makeText(MainActivity.this, " Email has been sent",
                            Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(MainActivity.this, "Failed...",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, " " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void signIn(String mail, String password) {
        pd.setMessage("Logging In...");

        pd.show();

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    pd.dismiss();
                    updateUI();


                }

                else  {
                    pd.dismiss();
                    showMessage(task.getException().getMessage());
                }

            }
        });


    }

    private void updateUI() {

        startActivity(home);

        finish();


    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if( user != null) {
            //user doesn't need to login everytime if they logged in already

            updateUI();

        }



    }
}
