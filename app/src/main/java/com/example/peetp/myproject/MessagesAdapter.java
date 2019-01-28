package com.example.peetp.myproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peetp.myproject.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private FirebaseAuth mAuth;
    private String current_id;


    private List<Messages> messagesList;

    public MessagesAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;

    }

    public void getId(){
        mAuth = FirebaseAuth.getInstance();
        current_id = mAuth.getCurrentUser().getUid();
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = (Messages) messagesList.get(position);
        getId();

        if(messages.getSender().equals(current_id)){
            return VIEW_TYPE_MESSAGE_SENT;
        }else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_holder_me, viewGroup, false);

            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_holder_you, viewGroup, false);

            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Messages messages = messagesList.get(position);

        switch (viewHolder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(messages);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(messages);
                break;
        }


    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText, nameText;
        CircleImageView profileImage;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.you_chat_text);
            timeText = (TextView) itemView.findViewById(R.id.you_time);
            profileImage = (CircleImageView) itemView.findViewById(R.id.you_profile);
        }

        void bind(Messages messages){
            messageText.setText(messages.getMessage());
            timeText.setText(messages.getTime());
            Picasso.get().load(messages.getProfileimage()).into(profileImage);
        }
    }

    public class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText, nameText;
        CircleImageView profileImage;

        public SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.me_chat_text);
            timeText = (TextView) itemView.findViewById(R.id.me_time);
        }

        void bind(Messages messages){
            messageText.setText(messages.getMessage());
            timeText.setText(messages.getTime());
        }
    }
}
