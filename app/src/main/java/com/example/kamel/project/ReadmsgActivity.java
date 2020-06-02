package com.example.kamel.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kamel.project.Fragments.Message;
import com.example.kamel.project.Model.BaseUtil;
import com.example.kamel.project.Model.Mails;

public class ReadmsgActivity extends AppCompatActivity {

    private TextView textView_detailedMsg;
    private String msgkey;
    private Mails selectedMail;
    private Button delete;
    public static String KEY_INTENT_COMPOSE_REPLY="KEY_INTENT_COMPOSE_REPLY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readmsg);
        setTitle(R.string.readMsg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.selectedMail = (Mails) getIntent().getExtras().getSerializable(Message.KEY_INTENT_READMSG);
        this.msgkey=selectedMail.getMsgkey();

        textView_detailedMsg=findViewById(R.id.textView_detailedMsg);

        textView_detailedMsg.setText(selectedMail.getDetailedMessage());

        delete = findViewById(R.id.deletemsg);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMail();
            }
        });

    }

    public void deleteMail(){
        BaseUtil.getmThreadRef().child(msgkey).removeValue();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
