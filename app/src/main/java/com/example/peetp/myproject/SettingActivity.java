package com.example.peetp.myproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, fullName, major, status, mobileNumber;
    private Button updateAccountBtn;
    private CircleImageView userProfileImg;

    private DatabaseReference settingsUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ตั้งค่า");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        fullName = (EditText) findViewById(R.id.settings_fullname);
        major = (EditText) findViewById(R.id.settings_major);
        status = (EditText) findViewById(R.id.settings_status);
        mobileNumber = (EditText) findViewById(R.id.settings_phone);
        userProfileImg = (CircleImageView) findViewById(R.id.settings_profile_image);
        updateAccountBtn = (Button) findViewById(R.id.setting_update_btn);

        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myFullName = dataSnapshot.child("fullname").getValue().toString();
                    String myMajor = dataSnapshot.child("major").getValue().toString();
                    String myMobileNumber = dataSnapshot.child("mobilenumber").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImg);
                    userName.setText(myUserName);
                    fullName.setText(myFullName);
                    major.setText(myMajor);
                    status.setText(myStatus);
                    mobileNumber.setText(myMobileNumber);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
