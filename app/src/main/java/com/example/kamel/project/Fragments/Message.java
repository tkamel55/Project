package com.example.kamel.project.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kamel.project.Adapter.MessageAdapter;
import com.example.kamel.project.Model.BaseUtil;
import com.example.kamel.project.Model.Mails;
import com.example.kamel.project.R;
import com.example.kamel.project.ReadmsgActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Message#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Message extends Fragment {

    DatabaseReference databaseReference;
    ArrayList<Mails> mailsArrayList;
    ListView listView_mails;
    public static String KEY_INTENT_READMSG="KEY_INTENT_READMSG";


    public Message() {
        // Required empty public constructor
    }


    public static Message newInstance(String param1, String param2) {
        Message fragment = new Message();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_message, container, false);


        listView_mails=fragmentView.findViewById(R.id.listView_mails);


        listView_mails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Mails selectedMail = mailsArrayList.get(i);
                BaseUtil.getmThreadRef().child(selectedMail.getMsgkey()).child(BaseUtil.MESSAGE_READ).setValue(Boolean.TRUE);
                Intent intent = new Intent(getActivity(), ReadmsgActivity.class);
                intent.putExtra(KEY_INTENT_READMSG,selectedMail);
                startActivity(intent);
            }
        });

        BaseUtil.getmThreadRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mailsArrayList = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Mails value = child.getValue(Mails.class);
                    mailsArrayList.add(value);
                }

                Collections.reverse(mailsArrayList);
                setMailsAdapter(mailsArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return  fragmentView ;
    }
    public void setMailsAdapter(ArrayList<Mails> mails){
        MessageAdapter adapter = new MessageAdapter(getActivity(),R.layout.adapter_mails_list,mails);
        listView_mails.setAdapter(adapter);

    }

}
