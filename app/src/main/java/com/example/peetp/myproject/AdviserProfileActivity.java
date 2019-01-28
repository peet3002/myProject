package com.example.peetp.myproject;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peetp.myproject.call.VoiceCallScreenActivity;
import com.example.peetp.myproject.model.Users;
import com.example.peetp.myproject.videocall.BaseActivity;
import com.example.peetp.myproject.videocall.CallScreenActivity;
import com.example.peetp.myproject.videocall.SinchService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdviserProfileActivity extends BaseActivity {

    private TextView userName, userFullname, userStatus, userMajor,userMobilePhone, userOffice, userKey;
    private ImageView   userOnlineStatus , userOfflineStatus;
    private CircleImageView userProfileImg;
    private DatabaseReference  userRef, adviserRef;
    private FirebaseAuth mAuth;
    private String current_user_id, adviserKey , dataKeys = "";;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adviser_profile);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        adviserRef = FirebaseDatabase.getInstance().getReference().child("Adviser").child(current_user_id);


        userName = (TextView) findViewById(R.id.adv_person_name);
        userFullname = (TextView) findViewById(R.id.adv_person_full_name);
        userStatus = (TextView) findViewById(R.id.adv_person_status);
        userMajor = (TextView) findViewById(R.id.adv_person_major);
        userMobilePhone = (TextView) findViewById(R.id.adv_person_moblie);
        userProfileImg = (CircleImageView) findViewById(R.id.adv_person_image);
        userOffice = (TextView) findViewById(R.id.adv_person_office);
        userOnlineStatus = (ImageView) findViewById(R.id.img_on);
        userOfflineStatus = (ImageView) findViewById(R.id.img_off);
        userKey = (TextView) findViewById(R.id.adv_key);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.adv_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_chat:
                        sendUserToChatActivity();
                        break;
                    case R.id.action_call:
                        if(userOnlineStatus.getVisibility() == View.VISIBLE){
                            voiceCallButtonClicked();
                        }
                        else{
                            Toast.makeText(AdviserProfileActivity.this, "ขออภัยอาจารย์ที่ปรึกษาไม่ได้ออนไลน์อยู่ในขณะนี้",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.action_videocall:
                        if(userOnlineStatus.getVisibility() == View.VISIBLE){
                            callButtonClicked();
                        }
                        else{
                            Toast.makeText(AdviserProfileActivity.this, "ขออภัยอาจารย์ที่ปรึกษาไม่ได้ออนไลน์อยู่ในขณะนี้",Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
                return true;
            }
        });

        adviserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String key = dataSnapshot.getKey();


                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        //Object object = child.getKey();

                        dataKeys=dataKeys+child.getKey() + "";
                    }

                    userKey.setText(dataKeys);




                    userRef.child(dataKeys).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Users data = dataSnapshot.getValue(Users.class);

                                Picasso.get().load(data.getProfileimage()).placeholder(R.drawable.profile).into(userProfileImg);
                                userMajor.setText(data.getMajor());
                                userName.setText(data.getUsername());
                                userFullname.setText(data.getFullname());
                                userStatus.setText(data.getStatus());
                                userMobilePhone.setText(data.getMobilenumber());
                                userOffice.setText(data.getOffice());

                                if(data.getOnlinestatus().equals("online")){
                                    userOnlineStatus.setVisibility(View.VISIBLE);
                                    userOfflineStatus.setVisibility(View.GONE);
                                }else {
                                    userOnlineStatus.setVisibility(View.GONE);
                                    userOfflineStatus.setVisibility(View.VISIBLE);
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    private void callButtonClicked() {
        String Key = userKey.getText().toString();
        Call call = getSinchServiceInterface().callUserVideo(Key);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    private void voiceCallButtonClicked() {
        String Key = userKey.getText().toString();
        Call call = getSinchServiceInterface().callUser(Key);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, VoiceCallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    private void sendUserToChatActivity() {
        Intent intent = new Intent(AdviserProfileActivity.this, ChatActivity.class);
        intent.putExtra("AdviserKey",dataKeys);
        intent.putExtra("Username",userName.getText().toString());
        startActivity(intent);

    }
}
