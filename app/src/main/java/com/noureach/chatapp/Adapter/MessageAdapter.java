package com.noureach.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noureach.chatapp.Model.Chat;
import com.noureach.chatapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChats;
    private String imageURL;

    FirebaseUser fUser;

    public MessageAdapter(Context context, List<Chat> chats, String image){
        mContext = context;
        mChats = chats;
        imageURL = image;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Chat chat = mChats.get(position);
        holder.showMessage.setText(chat.getMessage());

        if (imageURL.equals("default")){
            holder.profileImage.setImageResource(R.drawable.ic_default_image);
        }else {
            Glide.with(mContext).load(imageURL).into(holder.profileImage);
        }

        if (position == mChats.size()-1){
            if (chat.isIsseen()){
                holder.tvSeen.setText("seen");
            }else {
                holder.tvSeen.setText("Delivered");
            }
        }else {
            holder.tvSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public TextView showMessage;
        public TextView tvSeen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            showMessage = itemView.findViewById(R.id.show_message);
            tvSeen = itemView.findViewById(R.id.tvSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}

