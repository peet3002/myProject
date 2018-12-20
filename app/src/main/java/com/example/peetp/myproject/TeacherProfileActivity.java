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

public class TeacherProfileActivity extends AppCompatActivity {

    private TextView userName, userFullname, userStatus, userMajor,userMobilePhone, userOffice;

    private CircleImageView userProfileImg;
    private ImageButton imageButton;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        IntializeFields();
        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                String myUserName = dataSnapshot.child("username").getValue().toString();
                String myFullName = dataSnapshot.child("fullname").getValue().toString();
                String myMajor = dataSnapshot.child("major").getValue().toString();
                String myMobileNumber = dataSnapshot.child("mobilenumber").getValue().toString();
                String myStatus = dataSnapshot.child("status").getValue().toString();
                String myOffice = dataSnapshot.child("office").getValue().toString();

                Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImg);
                userMajor.setText(myMajor);
                userName.setText(myUserName);
                userFullname.setText(myFullName);
                userStatus.setText(myStatus);
                userMobilePhone.setText(myMobileNumber);
                userOffice.setText(myOffice);


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

    private void IntializeFields() {
        userName = (TextView) findViewById(R.id.teacher_profile_name);
        userFullname = (TextView) findViewById(R.id.teacher_profile_full_name);
        userStatus = (TextView) findViewById(R.id.teacher_profile_status);
        userMajor = (TextView) findViewById(R.id.teacher_profile_major);
        userMobilePhone = (TextView) findViewById(R.id.teacher_profile_moblie);
        userProfileImg = (CircleImageView) findViewById(R.id.teacher_profile_image);
        userOffice = (TextView) findViewById(R.id.teacher_profile_office);
        imageButton = (ImageButton) findViewById(R.id.teacher_profile_img_btn);
    }

    private void sendUserToSettingActivity() {
        Intent settingIntent = new Intent(TeacherProfileActivity.this, SettingTeacherActivity.class);
        startActivity(settingIntent);
    }
}
