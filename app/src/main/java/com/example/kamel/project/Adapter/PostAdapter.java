package com.example.kamel.project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kamel.project.Model.Post;
import com.example.kamel.project.PostDetailActivity;
import com.example.kamel.project.R;

import java.util.ArrayList;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    List<Post> mData;
    List<Post> mDataFiltered ;


    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataFiltered = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvTitle.setText(mDataFiltered.get(position).getTitle());
        Glide.with(mContext).load(mDataFiltered.get(position).getPicture()).into(holder.imgPost);

        String userImg = mDataFiltered.get(position).getUserPhoto();

        if (userImg != null) {
            Glide.with(mContext).load(userImg).into(holder.imgPostProfile);

        }
         else

        Glide.with(mContext).load(R.drawable.icon3).into(holder.imgPostProfile);








    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String Key = constraint.toString();
                if (Key.isEmpty()) {

                    mDataFiltered = mData;

                } else {
                    List<Post> lstFiltered = new ArrayList<>();
                    for (Post row : mData) {

                        if (row.getTitle().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }

                    }

                    mDataFiltered = lstFiltered;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataFiltered = (List<Post>) results.values;
                notifyDataSetChanged();
            }
        };

    }


            public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgPost;
        ImageView imgPostProfile;

        public  MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailsActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailsActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailsActivity.putExtra("postImage",mData.get(position).getPicture() );
                    postDetailsActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailsActivity.putExtra("postKey", mData.get(position).getPostKey());
                    postDetailsActivity.putExtra("Photo", mData.get(position).getUserPhoto());
                    //   postDetails.putExtra("userName",mData.get(position).getUsername);

                    long timestamp = (long)mData.get(position).getTimeStamp();
                    postDetailsActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailsActivity);




                }
            });




        }
    }
}
