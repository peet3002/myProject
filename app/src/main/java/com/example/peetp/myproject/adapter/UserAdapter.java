package com.example.peetp.myproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peetp.myproject.ChatActivity;
import com.example.peetp.myproject.R;
import com.example.peetp.myproject.model.MessageList;
import com.example.peetp.myproject.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> usersList;
    private boolean ischat;

    private FirebaseAuth mAuth;
    String theLastMessage, theDateMessage;

    public UserAdapter(Context mContext, List<Users> usersList, boolean ischat) {
        this.mContext = mContext;
        this.usersList = usersList;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_messages, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Users users = usersList.get(i);

        viewHolder.username.setText(users.getUsername());
        Picasso.get().load(users.getProfileimage()).into(viewHolder.profileImage);

        if(ischat){
            lastMessage(users.getKey(), viewHolder.lastMessage, viewHolder.time);
        }else {
            viewHolder.lastMessage.setVisibility(View.GONE);
            viewHolder.time.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("AdviserKey", users.getKey());
                intent.putExtra("Username", users.getUsername());
                mContext.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username, lastMessage , time;
        CircleImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.item_msg_username);
            profileImage = itemView.findViewById(R.id.item_msg_image);
            lastMessage = itemView.findViewById(R.id.item_msg_messages);
            time = itemView.findViewById(R.id.item_msg_date);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg,  final TextView date){
        theLastMessage = "default";
        theDateMessage = "default";
        mAuth = FirebaseAuth.getInstance();
        final String current_id = mAuth.getCurrentUser().getUid();
        final DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("MessageList").child(current_id);

        listRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                MessageList messageList = snapshot.getValue(MessageList.class);
                                if(messageList.getReceiver().equals(current_id) && messageList.getId().equals(userid) ||
                                        messageList.getReceiver().equals(userid) && messageList.getId().equals(current_id)){
                                    theLastMessage = messageList.getMessage();
                                    theDateMessage = messageList.getTime();
                                }
                            }

                            switch (theLastMessage){
                                case "default":
                                    last_msg.setText("No Message");
                                    date.setText("");
                                    break;
                                default:
                                    last_msg.setText(theLastMessage);
                                    date.setText(theDateMessage);
                                    break;
                            }

                            theLastMessage = "default";
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

}



