package com.example.kamel.project.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kamel.project.Model.CategorieModel;
import com.example.kamel.project.R;
import com.example.kamel.project.SetActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {
    private List<CategorieModel> categoryModelList;

    public CategoryAdapter(List<CategorieModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        holder.setData(categoryModelList.get(position).getUrl(), categoryModelList.get(position).getName(), categoryModelList.get(position).getSets(), categoryModelList.get(position).getKey(), position);
    }


    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    class viewholder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private TextView title;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title);

        }

        private void setData(String url, final String title, final int sets, final String key, final int position) {
            Glide.with(itemView.getContext()).load(url).into(imageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent = new Intent(itemView.getContext(), SetActivity.class);

                    setIntent.putExtra("title", title);
                    setIntent.putExtra("sets", sets);


                    itemView.getContext().startActivity(setIntent);
                }
            });

        }


    }

}
