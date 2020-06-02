package com.example.kamel.project.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kamel.project.AdminQuestionActivity;
import com.example.kamel.project.R;

public class AdminGridAdapter extends BaseAdapter {

    public int sets=0;
    private String category;
    private GrideListener listener;


    public AdminGridAdapter(int sets, String category, GrideListener listener) {
        this.sets = sets;
        this.category=category;
        this.listener=listener;
    }



    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public int getCount() {
        return sets+1;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view;
        if (convertView==null)
        {
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adminset_item,parent,false);
        }else
        {
            view=convertView;
        }
        if (position==0)
        {
            ((TextView)view.findViewById(R.id.textView6)).setText("+");
        }else
        {
            ((TextView)view.findViewById(R.id.textView6)).setText(String.valueOf(position));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position==0)
                {
                    //add set code
                    listener.addSet();
                }else {
                    Intent qusIntent=new Intent(parent.getContext(), AdminQuestionActivity.class);
                    qusIntent.putExtra("category",category);
                    qusIntent.putExtra("setNo",position);
                    parent.getContext().startActivity(qusIntent);
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (position!=0)
                {
                    listener.onLongClick(position);
                }
                return false;
            }
        });

        return view;
    }

    public interface GrideListener{
        public void addSet();

        public void onLongClick(int setNo);
    }


}
