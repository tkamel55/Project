package com.example.kamel.project;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamel.project.Model.QuestionModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    private TextView question, noIndicator;
    private LinearLayout optionsContainer;
    private Button NextBtn;
    private int count = 0;
    private int position = 0;

    private int score = 0;
    private String category;
    private int setNo;

    private List<QuestionModel> list;
    private Dialog loadingDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_quiz);
        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.no_indicator);
        optionsContainer = findViewById(R.id.options_container);
        NextBtn = findViewById(R.id.next_btn);

        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo", 1);

        getSupportActionBar().hide();


        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));

        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        list = new ArrayList<>();

        loadingDialog.show();
        ref.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(QuestionModel.class));
                }

                if (list.size() > 0) {


                    for (int i = 0; i < 4; i++) {

                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {

                                checkAnswer((Button) v);
                            }
                        });
                    }

                    playAnim(question, 0, list.get(position).getQuestions());


                    NextBtn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {

                            NextBtn.setEnabled(false);
                            NextBtn.setAlpha(0.7f);
                            enableOption(true);
                            position++;
                            if (position == list.size()) {
                                //score activity
                                Intent scoreIntent = new Intent(QuizActivity.this, ScoreActivity.class);
                                scoreIntent.putExtra("score", score);
                                scoreIntent.putExtra("total", list.size());
                                startActivity(scoreIntent);

                                finish();

                                return;
                            }
                            count = 0;
                            playAnim(question, 0, list.get(position).getQuestions());
                        }
                    });

                } else {
                    finish();
                    Toast.makeText(QuizActivity.this, "no questions!!", Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAnswer(Button selectedOption) {
        enableOption(false);
        NextBtn.setEnabled(true);
        NextBtn.setAlpha(1);
        if (selectedOption.getText().toString().equals(list.get(position).getCorrectAns())) {

            //if the answer is correct
            score++;
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));

        } else {
            //if the answer is incorrect the color will change
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("red")));

            Button correctOption = (Button) optionsContainer.findViewWithTag(list.get(position).getCorrectAns());
            correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("green")));

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enableOption(boolean enable) {

        for (int i = 0; i < 4; i++) {

            optionsContainer.getChildAt(i).setEnabled(enable);

            if (enable) {
                optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("gray")));


            }

        }

    }


    private void playAnim(final View view, final int value, final String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (value == 0 && count < 4) {
                    String option = "";

                    if (count == 0) {
                        option = list.get(position).getOptionA();


                    } else if (count == 1) {
                        option = list.get(position).getOptionB();

                    } else if (count == 2) {
                        option = list.get(position).getOptionC();

                    } else if (count == 3) {
                        option = list.get(position).getOptionD();

                    }

                    playAnim(optionsContainer.getChildAt(count), 0, option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //changing data
                if (value == 0) {

                    try {

                        ((TextView) view).setText(data);
                        noIndicator.setText(position + 1 + "/" + list.size());


                    } catch (ClassCastException ex) {

                        ((Button) view).setText(data);

                    }
                    view.setTag(data);

                    playAnim(view, 1, data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }
}
