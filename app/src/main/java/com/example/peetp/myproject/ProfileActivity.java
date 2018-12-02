package com.example.peetp.myproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userFullname, userStatus, userMajor, userUid, userMobilePhone;
    private CircleImageView userProfileImg;
    private ImageButton imageButton;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);


        userName = (TextView) findViewById(R.id.profile_name);
        userFullname = (TextView) findViewById(R.id.profile_full_name);
        userStatus = (TextView) findViewById(R.id.profile_status);
        userMajor = (TextView) findViewById(R.id.profile_major);
        userUid = (TextView) findViewById(R.id.profile_uid);
        userMobilePhone = (TextView) findViewById(R.id.profile_moblie);
        userProfileImg = (CircleImageView) findViewById(R.id.profile_image);

        imageButton = (ImageButton) findViewById(R.id.profile_img_btn);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUid = dataSnapshot.child("uid").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myFullName = dataSnapshot.child("fullname").getValue().toString();
                    String myMajor = dataSnapshot.child("major").getValue().toString();
                    String myMobileNumber = dataSnapshot.child("mobilenumber").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImg);
                    userUid.setText(myUid);
                    userMajor.setText(myMajor);
                    userName.setText(myUserName);
                    userFullname.setText(myFullName);
                    userStatus.setText(myStatus);
                    userMobilePhone.setText(myMobileNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToSettingActivity();
            }
        });
    }

    private void sendUserToSettingActivity() {
        Intent settingIntent = new Intent(ProfileActivity.this, SettingActivity.class);
        startActivity(settingIntent);
    }
}
