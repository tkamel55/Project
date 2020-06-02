package com.example.kamel.project.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kamel.project.Model.AdminQuestionModel;
import com.example.kamel.project.R;
import com.example.kamel.project.addquestion;

import java.util.List;

public class AdminQuestionsAdapter extends RecyclerView.Adapter<AdminQuestionsAdapter.Viewholder> {

    private List<AdminQuestionModel> list;
    private String category ;
    private DeleteListener listener;

    public AdminQuestionsAdapter(List<AdminQuestionModel> list, String category, DeleteListener listner) {
        this.list = list;
        this.category = category;
        this.listener = listner;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent,false);
        return  new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String question = list.get(position).getQuestions();
        String answer = list.get(position).getCorrectAns();

        holder.setData(question, answer, position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class Viewholder extends RecyclerView.ViewHolder{
        private TextView question, answer;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);


        }
        private void setData(String question, String answer, final int position){
            this.question.setText(position + 1 + " ." + question);
            this.answer.setText("Ans ." + answer);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(itemView.getContext(), addquestion.class );
                    editIntent.putExtra("categoryName", category);
                    editIntent.putExtra("setNo", list.get(position).getSetNo());
                    editIntent.putExtra("position", position);
                    itemView.getContext().startActivity(editIntent);


                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    listener.onLongClick(position, list.get(position).getId());


                    return false;
                }
            });

        }
    }

    public interface DeleteListener  {
        void onLongClick (int position, String id );
    }

}
