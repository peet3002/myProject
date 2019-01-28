package com.example.peetp.myproject.call;


import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.widget.ImageButton;

import com.example.peetp.myproject.R;
import com.example.peetp.myproject.model.Users;
import com.example.peetp.myproject.videocall.AudioPlayer;
import com.example.peetp.myproject.videocall.BaseActivity;
import com.example.peetp.myproject.videocall.SinchService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class VoiceIncomingCallScreenActivity extends BaseActivity {

    static final String TAG = VoiceIncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    private DatabaseReference usersRef;
    private ImageView imageView;
    private TextView remoteUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incoming_call_screen);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ImageButton answer = (ImageButton) findViewById(R.id.voiceanswerButton);
        answer.setOnClickListener(mClickListener);
        ImageButton decline = (ImageButton) findViewById(R.id.voicedeclineButton);
        decline.setOnClickListener(mClickListener);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null) {
            if (getIntent().getStringExtra(SinchService.CALL_ID) != null) {
                mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
            }
        }
    }


    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            remoteUser = (TextView) findViewById(R.id.voiceremoteUser);
            imageView = (ImageView) findViewById(R.id.remote_user_image);
            remoteUser.setText(call.getRemoteUserId());
            usersRef.child(call.getRemoteUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Users users = dataSnapshot.getValue(Users.class);
                        remoteUser.setText(users.getUsername());
                        Picasso.get().load(users.getProfileimage()).into(imageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            Log.d(TAG, "Answering call");
            call.answer();
            Intent intent = new Intent(this, VoiceCallScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
//            Intent mainActivity = new Intent(VoiceIncomingCallScreenActivity.this, VoicePlaceCallActivity.class);
//            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(mainActivity);
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // no need to implement for managed push
        }

    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.voiceanswerButton:
                    answerClicked();
                    break;
                case R.id.voicedeclineButton:
                    declineClicked();
                    break;
            }
        }
    };
}
