package com.example.kamel.project;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamel.project.Model.Mails;
import com.example.kamel.project.Model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.example.kamel.project.Model.BaseUtil.*;


public class SendingMailActivity extends AppCompatActivity {
    String recipient;
    String sender;
    String key=null;
    Boolean isReply = Boolean.FALSE;
    private TextView textView_receipient;
    private EditText editText_compose_msg;
    private ImageButton imageButton_chooseRecipient;
    private Button button_send;
    private ArrayList<String> recipent_list;
    private ArrayList<String> usersKey;
    private AlertDialog.Builder alertDialog;
    Mails selectedMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_mail);
        setTitle("Send Mail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras() != null) {

            selectedMail = ( Mails) getIntent().getExtras().getSerializable(ReadmsgActivity.KEY_INTENT_COMPOSE_REPLY);
            recipient=selectedMail.getSenderName();
            key=selectedMail.getSentkey();
            isReply = Boolean.TRUE;

        }

        textView_receipient = findViewById(R.id.textView_compose_recipient);
        editText_compose_msg = findViewById(R.id.editText_mailContent);
        imageButton_chooseRecipient = findViewById(R.id.imageButton_chooseRecipient);
        button_send = findViewById(R.id.button_sendMail);

        if (isReply) {
            textView_receipient.setText(recipient);
            imageButton_chooseRecipient.setEnabled(Boolean.FALSE);
        }

        imageButton_chooseRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getmDatabase().child(DB_NAME_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            recipent_list=new ArrayList<>();
                            usersKey=new ArrayList<>();
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for (DataSnapshot child : children) {
                                Student value = child.getValue(Student.class);
                                recipent_list.add(value.getUser_email() + " " + value.getUser_name());
                                usersKey.add(value.getUser_key());
                            }

                            final String str[] = recipent_list.toArray(new String[recipent_list.size()]);

                            alertDialog = new AlertDialog.Builder(SendingMailActivity.this);

                            alertDialog.setTitle(R.string.usersList).
                                    setItems(str, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SendingMailActivity.this.recipient = recipent_list.get(i);
                                            SendingMailActivity.this.key = usersKey.get(i);
                                            textView_receipient.setText(SendingMailActivity.this.recipient);
                                        }
                                    });

                            alertDialog.create();
                            alertDialog.show();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key != null) {
                    String msg = editText_compose_msg.getText().toString();

                    Map<String,Object> timestamp = new HashMap<String,Object>();
                    timestamp.put("timestamp", ServerValue.TIMESTAMP);


                    Mails mails = new Mails(SendingMailActivity.this.sender, msg, Boolean.FALSE);
                    String msgKey = getmDatabase().child(DB_NAME_MESSAGES).child(key).push().getKey();
                    mails.setMsgkey(msgKey);
                    mails.setMailTimeStamp(timestamp);

                    getmDatabase().child(DB_NAME_MESSAGES).child(key).child(mails.getMsgkey()).setValue(mails);
                    Toast.makeText(SendingMailActivity.this, R.string.sentToast, Toast.LENGTH_SHORT).show();
                   // finish();
                } else {
                    Toast.makeText(SendingMailActivity.this, R.string.contact_blank, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
