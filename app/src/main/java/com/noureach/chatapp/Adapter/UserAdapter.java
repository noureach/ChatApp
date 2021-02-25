package com.noureach.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noureach.chatapp.MessageActivity;
import com.noureach.chatapp.Model.Chat;
import com.noureach.chatapp.Model.User;
import com.noureach.chatapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;

    String theLatestMessage;

    public UserAdapter(Context context, List<User> users, boolean isChat){
        mContext = context;
        mUsers = users;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){
            holder.profileImage.setImageResource(R.drawable.ic_default_image);
        }else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profileImage);
        }

        if (isChat){
            latestMessage(user.getId(), holder.latestMsg, holder.countMsg);
        }else {
            holder.latestMsg.setVisibility(View.GONE);
        }

        if (isChat){
            if (user.getStatus().equals("online")){
                holder.imageOn.setVisibility(View.VISIBLE);
                holder.imageOff.setVisibility(View.GONE);
            }else {
                holder.imageOn.setVisibility(View.GONE);
                holder.imageOff.setVisibility(View.VISIBLE);
            }
        }else {
            holder.imageOn.setVisibility(View.GONE);
            holder.imageOff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public TextView username;
        public ImageView imageOn;
        public ImageView imageOff;
        public TextView latestMsg;
        public TextView countMsg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image_user);
            username = itemView.findViewById(R.id.username_user);
            imageOn = itemView.findViewById(R.id.img_on);
            imageOff = itemView.findViewById(R.id.img_off);
            latestMsg = itemView.findViewById(R.id.last_smg);
            countMsg = itemView.findViewById(R.id.tvCount);
        }
    }

    private void latestMessage(String userId, TextView latest_message, TextView countSmg){
        theLatestMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        if (firebaseUser != null)
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        if (firebaseUser.getUid().equals(chat.getSender())) {
                            theLatestMessage = "You: " + chat.getMessage();
                        } else {
                            theLatestMessage = chat.getMessage();
                        }

                        if (!chat.isIsseen() && firebaseUser.getUid().equals(chat.getReceiver())) {
                            latest_message.setTypeface(null, Typeface.BOLD);
                            latest_message.setTextColor(Color.BLACK);
                            countSmg.setVisibility(View.VISIBLE);
                            unread++;
                            countSmg.setText(unread+"");
                        } else {
                            latest_message.setTypeface(null, Typeface.NORMAL);
                            countSmg.setVisibility(View.GONE);
                        }
                    }
                }

                switch (theLatestMessage){
                    case "default" :
                        latest_message.setText("No message");
                        break;
                    default:
                        latest_message.setText(theLatestMessage);
                        break;
                }

                theLatestMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
