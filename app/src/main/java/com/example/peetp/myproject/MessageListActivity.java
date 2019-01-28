package com.example.peetp.myproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peetp.myproject.adapter.UserAdapter;
import com.example.peetp.myproject.model.MessageList;
import com.example.peetp.myproject.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference reference, userRef;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        reference = FirebaseDatabase.getInstance().getReference().child("MessageList").child(currentUserId);

        recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mToolbar = (Toolbar) findViewById(R.id.message_list_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("ข้อความ");

        sort();
    }

    private void sort() {
        Query sort = reference.orderByChild("date");
        adapter(sort);
    }

    private void adapter(Query sort){
        FirebaseRecyclerOptions<MessageList> options =
                new FirebaseRecyclerOptions.Builder<MessageList>()
                        .setQuery(sort, MessageList.class)
                        .build();

        FirebaseRecyclerAdapter<MessageList, MessageListHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<MessageList, MessageListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageListHolder holder, int position, @NonNull MessageList model) {
                final String PostKey = getRef(position).getKey();
                holder.username.setText(model.getUsername());
                holder.time.setText(model.getTime());
                holder.messageText.setText(model.getMessage());
                Picasso.get().load(model.getProfileimage()).into(holder.profileImage);

                userRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Users data = dataSnapshot.getValue(Users.class);

                            if(data.getOnlinestatus().equals("online")){
                                holder.userOnlineStatus.setVisibility(View.VISIBLE);
                                holder.userOfflineStatus.setVisibility(View.GONE);
                            }else {
                                holder.userOnlineStatus.setVisibility(View.GONE);
                                holder.userOfflineStatus.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailIntent = new Intent(MessageListActivity.this, ChatActivity.class);
                        detailIntent.putExtra("AdviserKey",PostKey);
                        detailIntent.putExtra("Username",holder.username.getText().toString());
                        startActivity(detailIntent);

                    }
                });
            }

            @NonNull
            @Override
            public MessageListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_messages, viewGroup, false);
                MessageListHolder viewHolder = new MessageListHolder(view);
                return viewHolder;
            }


        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class MessageListHolder extends RecyclerView.ViewHolder{
        TextView username, time, messageText;
        CircleImageView profileImage;
        ImageView userOnlineStatus , userOfflineStatus;

        public MessageListHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.item_msg_username);
            time = (TextView) itemView.findViewById(R.id.item_msg_date);
            profileImage = (CircleImageView) itemView.findViewById(R.id.item_msg_image);
            messageText = (TextView) itemView.findViewById(R.id.item_msg_messages);
            userOnlineStatus = (ImageView) itemView.findViewById(R.id.img_online);
            userOfflineStatus = (ImageView) itemView.findViewById(R.id.img_offline);


        }

    }


}
