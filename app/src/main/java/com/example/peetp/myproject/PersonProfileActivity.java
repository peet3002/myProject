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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");

//        bottemMenu();
        IntializeFields();

        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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

                    MaintanaceofButtons();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cancelBtn.setVisibility(View.INVISIBLE);


       if(!senderUserId.equals(receiverUserId)){
           requestBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(CURRENT_STATE.equals("not_friends")){
                       sendFriendRequestToPerson();
                       Toast.makeText(PersonProfileActivity.this, "ส่งคำขอเรียบร้อย", Toast.LENGTH_SHORT).show();
                   }
               }
           });
           cancelBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) { ;
                   if(CURRENT_STATE.equals("request_sent")){
                       cancelFriendRequest();
                       Toast.makeText(PersonProfileActivity.this, "ยกเลิกคำขอ", Toast.LENGTH_SHORT).show();
                   }
               }
           });
       }
       else{
           cancelBtn.setVisibility(View.INVISIBLE);
           requestBtn.setVisibility(View.INVISIBLE);
       }
    }

    private void cancelFriendRequest() {
        friendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                CURRENT_STATE = "not_friends";
                                                requestBtn.setVisibility(View.VISIBLE);
                                                cancelBtn.setVisibility(View.INVISIBLE);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendFriendRequestToPerson() {
        friendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                CURRENT_STATE = "request_sent";
                                                requestBtn.setVisibility(View.INVISIBLE);
                                                cancelBtn.setVisibility(View.VISIBLE);

                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void MaintanaceofButtons() {
        friendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                            if(request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                requestBtn.setVisibility(View.INVISIBLE);
                                cancelBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

//    private void bottemMenu(){
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.action_request:
//                        Toast.makeText(PersonProfileActivity.this, "ส่งคำขอ", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.action_cancel:
//                        Toast.makeText(PersonProfileActivity.this, "ยกเลิก", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//
//
//                return true;
//            }
//        });
//    }



    private void IntializeFields() {

        userName = (TextView) findViewById(R.id.person_name);
        userFullname = (TextView) findViewById(R.id.person_full_name);
        userStatus = (TextView) findViewById(R.id.person_status);
        userMajor = (TextView) findViewById(R.id.person_major);
        userMobilePhone = (TextView) findViewById(R.id.person_moblie);
        userProfileImg = (CircleImageView) findViewById(R.id.person_image);
        userOffice = (TextView) findViewById(R.id.person_office);
        requestBtn = (ImageButton) findViewById(R.id.person_add);
        cancelBtn = (ImageButton) findViewById(R.id.person_cancel);

        CURRENT_STATE = "not_friends";
    }
}
