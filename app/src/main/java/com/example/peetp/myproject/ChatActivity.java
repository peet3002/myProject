package com.example.peetp.myproject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.peetp.myproject.notifications.Client;
import com.example.peetp.myproject.notifications.Data;
import com.example.peetp.myproject.notifications.MyResponse;
import com.example.peetp.myproject.notifications.Sender;
import com.example.peetp.myproject.notifications.Token;
import com.example.peetp.myproject.model.Messages;
import com.example.peetp.myproject.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button sendMessageBtn ;
    private EditText userMessageInput;

    private RecyclerView userMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private DatabaseReference usersRef, messageRef, rootRef;
    private FirebaseAuth mAuth;

    private String messageSenderID, chatText, messageReceiverID, saveCurrentDate , saveCurrentTime, saveCurrentYearDate, saveCurrentTime2, saveCurrentYear;
    private int year;

    APIService apiService;
    boolean notify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        rootRef  = FirebaseDatabase.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        messageReceiverID = getIntent().getExtras().get("AdviserKey").toString();
//        username = getIntent().getExtras().get("Username").toString();


        sendMessageBtn = (Button) findViewById(R.id.button_chatbox_send);
        userMessageInput = (EditText) findViewById(R.id.edittext_chatbox);

        userMessagesList = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        messagesAdapter = new MessagesAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);

        mToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usersRef.child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users users =  dataSnapshot.getValue(Users.class);
                    getSupportActionBar().setTitle(users.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        getSupportActionBar().setTitle(username);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        scrollRecyclerView();

        userMessageInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(messagesAdapter.getItemCount() != 0) {
                    userMessagesList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount() - 1);
                        }
                    }, 500);
                }
            }
        });

        userMessageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessagesList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount() - 1);
                    }
                },500);
            }
        });



        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                    usersRef.child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String userName = dataSnapshot.child("username").getValue().toString();
                                String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                                updateMessagesChildren(userName, userProfileImage);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }
        });
        
        fetchMessage();
        updateToken(FirebaseInstanceId.getInstance().getToken());
                
    }

    private void fetchMessage() {
        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.exists()){
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesAdapter.notifyDataSetChanged();
                            userMessagesList.post(new Runnable() {
                                @Override
                                public void run() {
                                    userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount() - 1);
                                }
                            });



                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(messageSenderID).setValue(token1);
    }

    private void updateMessagesChildren(final String userName, final String userProfileImage) {
        chatText = userMessageInput.getText().toString();
        if (TextUtils.isEmpty(chatText)) {
            Toast.makeText(this, "กรุณากรอกคอมเมนส์", Toast.LENGTH_SHORT).show();
        }else{
            String message_sender_ref = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String message_receiver_ref = "Messages/" + messageReceiverID + "/" + messageSenderID;


            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderID)
                    .child(messageReceiverID).push();


            String message_push_id = user_message_key.getKey();


            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM",new Locale("th"));
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordYear = Calendar.getInstance();
            SimpleDateFormat currentYear = new SimpleDateFormat("yyyy",new Locale("th"));
            saveCurrentYear = currentYear.format(calFordYear.getTime());
            year = Integer.parseInt(saveCurrentYear);
            year += 543;

            Calendar calFordYearDate = Calendar.getInstance();
            SimpleDateFormat currentYearDate = new SimpleDateFormat("MMdd", new Locale("th"));
            saveCurrentYearDate = currentYearDate.format(calFordYearDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(calFordDate.getTime());

            Calendar calFordTime2 = Calendar.getInstance();
            SimpleDateFormat currentTime2 = new SimpleDateFormat("HHmmss");
            saveCurrentTime2 = currentTime2.format(calFordDate.getTime());


            HashMap messageTextBody = new HashMap();
            messageTextBody.put("sender", messageSenderID);
            messageTextBody.put("receiver", messageReceiverID);
            messageTextBody.put("message", chatText);
            messageTextBody.put("date", saveCurrentDate + "-" + year);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("username", userName);
            messageTextBody.put("profileimage", userProfileImage);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + message_push_id , messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id , messageTextBody);

            rootRef.updateChildren(messageBodyDetails);
            final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("MessageList")
                    .child(messageReceiverID)
                    .child(messageSenderID);

            final DatabaseReference messageRef2 = FirebaseDatabase.getInstance().getReference("MessageList")
                    .child(messageSenderID)
                    .child(messageReceiverID);


            messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        messageRef.child("id").setValue(messageSenderID);
                        messageRef.child("message").setValue(chatText);
                        messageRef.child("receiver").setValue(messageReceiverID);
                        messageRef.child("date").setValue(year + saveCurrentYearDate + saveCurrentTime);
                        messageRef.child("time").setValue(saveCurrentTime);
                        messageRef.child("username").setValue(userName);
                        messageRef.child("profileimage").setValue(userProfileImage);
                        messageRef2.child("message").setValue(chatText);
                        messageRef2.child("date").setValue(year + saveCurrentYearDate + saveCurrentTime2);
                        messageRef2.child("time").setValue(saveCurrentTime);
                    }
                    else{
                        messageRef.child("message").setValue(chatText);
                        messageRef.child("date").setValue(year + saveCurrentYearDate + saveCurrentTime2);
                        messageRef.child("time").setValue(saveCurrentTime);
                        messageRef2.child("message").setValue(chatText);
                        messageRef.child("profileimage").setValue(userProfileImage);
                        messageRef.child("username").setValue(userName);
                        messageRef2.child("date").setValue(year +saveCurrentYearDate + saveCurrentTime2);
                        messageRef2.child("time").setValue(saveCurrentTime);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            userMessageInput.setText("");

            final String msg = chatText;
            usersRef.child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if(notify) {
                        sendNotification(messageReceiverID, users.getUsername(), msg);
                    }
                    notify = false;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(messageSenderID, R.mipmap.ic_launcher, username+": "+ message , "New Message",
                            messageReceiverID);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200 ){
                                        if(response.body().success != 1){
                                            Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void scrollRecyclerView(){
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount =  messagesAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    userMessagesList.scrollToPosition(positionStart);
                }
            }
        });
    }







}
