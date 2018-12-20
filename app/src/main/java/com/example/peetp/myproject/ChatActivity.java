package com.example.peetp.myproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;


public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolBar;
    private ImageButton sendMessageBtn, sendImageBtn;
    private EditText userMessageInput;
    private RecyclerView userMessageList;

    private String messageReceiverId, messageReceiverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        IntializeFields();
    }

    private void IntializeFields() {
        chatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);

        sendMessageBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        sendImageBtn = (ImageButton) findViewById(R.id.chat_attachment);
        userMessageInput = (EditText) findViewById(R.id.chat_message);

    }
}
