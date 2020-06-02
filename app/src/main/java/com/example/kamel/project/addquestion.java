package com.example.kamel.project;

import android.app.Dialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kamel.project.Model.AdminQuestionModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class addquestion extends AppCompatActivity {


    private EditText question;
    private RadioGroup options;
    private LinearLayout answers;
    private Button uploadBtn;
    private String categoryName;
    private int setNo, position;
    private Dialog loadingDialog;
    private AdminQuestionModel adminQuestionModel;
    private String id;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addquestion);



        getSupportActionBar().setTitle("Add question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        question = findViewById(R.id.question);
        options = findViewById(R.id.options);
        answers = findViewById(R.id.answers);
        uploadBtn = findViewById(R.id.upload);


        categoryName = getIntent().getStringExtra("categoryName");
        setNo = getIntent().getIntExtra("setNo", -1);
        position = getIntent().getIntExtra("position", -1);


        if (setNo == -1) {
            finish();
            return;
        }

        if (position != -1) {

            adminQuestionModel = AdminQuestionActivity.list.get(position);
            setData();


        }


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getText().toString().isEmpty()) {
                    question.setError("Required");
                    return;
                }
                upload();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setData() {

        question.setText(adminQuestionModel.getQuestions());

        ((EditText) answers.getChildAt(0)).setText(adminQuestionModel.getOptionA());
        ((EditText) answers.getChildAt(1)).setText(adminQuestionModel.getOptionB());
        ((EditText) answers.getChildAt(2)).setText(adminQuestionModel.getOptionC());
        ((EditText) answers.getChildAt(3)).setText(adminQuestionModel.getOptionD());

        for (int i = 0; i < answers.getChildCount(); i++) {
            if (((EditText) answers.getChildAt(i)).getText().toString().equals(adminQuestionModel.getCorrectAns())) {

                RadioButton radioButton = (RadioButton) options.getChildAt(i);
                radioButton.setChecked(true);
                break;
            }
        }
    }

    private void upload() {

        int correct = -1;
        for (int i = 0; i < options.getChildCount(); i++) {


            EditText answer = (EditText) answers.getChildAt(i);

            if (answer.getText().toString().isEmpty()) {

                answer.setError("Required");
                return;

            }

            RadioButton radioButton = (RadioButton) options.getChildAt(i);
            if (radioButton.isChecked()) {
                correct = i;
                break;
            }

        }
        if (correct == -1) {
            Toast.makeText(this, "Please mark the correct answer", Toast.LENGTH_SHORT).show();
            return;
        }
        final HashMap<String, Object> map = new HashMap<>();
        map.put("correctAns", ((EditText) answers.getChildAt(correct)).getText().toString());
        map.put("optionA", ((EditText) answers.getChildAt(0)).getText().toString());
        map.put("optionB", ((EditText) answers.getChildAt(1)).getText().toString());
        map.put("optionC", ((EditText) answers.getChildAt(2)).getText().toString());
        map.put("optionD", ((EditText) answers.getChildAt(3)).getText().toString());
        map.put("questions", question.getText().toString());
        map.put("setNo", setNo);

        if (position != -1) {

            id = adminQuestionModel.getId();


        } else {
            id = UUID.randomUUID().toString();
        }


        loadingDialog.show();


        FirebaseDatabase.getInstance().getReference().child("SETS")
                .child(categoryName).child("questions").child(id)
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    AdminQuestionModel questionModel = new AdminQuestionModel(id, map.get("questions").toString(),
                            map.get("optionA").toString(), map.get("optionB").toString(), map.get("optionC").toString(), map.get("optionD").toString(),
                            map.get("correctAns").toString(),
                            (int) map.get("setNo"));

                    if(position != -1 ) {
                        AdminQuestionActivity.list.set(position,questionModel);

                    } else {

                        AdminQuestionActivity.list.add(questionModel);

                    }

                    finish();


                } else {
                    Toast.makeText(addquestion.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                }
                loadingDialog.dismiss();

            }
        });


    }


}
