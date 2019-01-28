package com.example.peetp.myproject;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peetp.myproject.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userFullname, userStatus, userMajor,userMobilePhone, userOffice;
    private CircleImageView userProfileImg;
    private ImageButton requestBtn, cancelBtn;

    private DatabaseReference friendRequestRef, userRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId, CURRENT_STATE;
    private boolean isChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        IntializeFields();

        userRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void IntializeFields() {

        userName = (TextView) findViewById(R.id.person_name);
        userFullname = (TextView) findViewById(R.id.person_full_name);
        userStatus = (TextView) findViewById(R.id.person_status);
        userMajor = (TextView) findViewById(R.id.person_major);
        userMobilePhone = (TextView) findViewById(R.id.person_moblie);
        userProfileImg = (CircleImageView) findViewById(R.id.person_image);
        userOffice = (TextView) findViewById(R.id.person_office);



        CURRENT_STATE = "not_friends";
    }
}
