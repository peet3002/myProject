package com.example.peetp.myproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.peetp.myproject.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userFullname, userStatus, userMajor, userUid, userMobilePhone, userDegree, userSec;
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
        userDegree = (TextView) findViewById(R.id.profile_degree);
        userSec = (TextView) findViewById(R.id.profile_sec);
        userMobilePhone = (TextView) findViewById(R.id.profile_moblie);
        userProfileImg = (CircleImageView) findViewById(R.id.profile_image);


        imageButton = (ImageButton) findViewById(R.id.profile_img_btn);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users data = dataSnapshot.getValue(Users.class);

                    Picasso.get().load(data.getProfileimage()).placeholder(R.drawable.profile).into(userProfileImg);
                    userUid.setText(data.getUid());
                    userMajor.setText(data.getMajor());
                    userName.setText(data.getUsername());
                    userFullname.setText(data.getFullname());
                    userStatus.setText(data.getStatus());
                    userMobilePhone.setText(data.getMobilenumber());
                    userDegree.setText(data.getDegree());
                    userSec.setText(data.getSec());
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
        Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
        startActivity(intent);
    }
}
