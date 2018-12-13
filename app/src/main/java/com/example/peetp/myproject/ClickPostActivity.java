package com.example.peetp.myproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ImageView clickPostImage, clickPostProfileImg;
    private TextView clickPostDescription, clickPostUsername, clickPostDate, clickPostTime;
    private ImageButton clickPostEdit, clickPostDelete;

    private EditText commentText;
    private ImageButton postCommentBtn;
    private RecyclerView commentsList;

    private DatabaseReference clickRef,commentRef, usersRef;
    private FirebaseAuth mAuth;

    private String PostKey, currentUserId, databaseUserId, description, postImage, username, date, time, profileImage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostKey = getIntent().getExtras().get("PostKey").toString();
        clickRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        commentRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Comments");

        clickPostUsername =  (TextView) findViewById(R.id.click_profile_name);
        clickPostDate = (TextView) findViewById(R.id.click_post_date);
        clickPostTime = (TextView) findViewById(R.id.click_post_time);
        clickPostProfileImg = (ImageView) findViewById(R.id.click_profile_img) ;
        clickPostImage = (ImageView) findViewById(R.id.click_post_image);
        clickPostDescription = (TextView) findViewById(R.id.click_post_description);
        clickPostEdit = (ImageButton) findViewById(R.id.click_post_edit);
        clickPostDelete = (ImageButton) findViewById(R.id.click_post_delete);

//        commentText = (EditText) findViewById(R.id.click_add_comment);
//        postCommentBtn = (ImageButton) findViewById(R.id.click_post);

        commentsList = (RecyclerView) findViewById(R.id.click_recycler_view);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        commentsList.setLayoutManager(linearLayoutManager);

        mToolbar = (Toolbar) findViewById(R.id.click_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        final ScrollView main = (ScrollView) findViewById(R.id.scrollView1);

        main.post(new Runnable() {
            @Override
            public void run() {
                main.scrollTo(0,0);
            }
        });


        clickPostDelete.setVisibility(View.INVISIBLE);
        clickPostEdit.setVisibility(View.INVISIBLE);

        displsyAllUsersComment();

//        postCommentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            String userName = dataSnapshot.child("username").getValue().toString();
//                            String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();
//
//                            validateComment(userName, userProfileImage);
//
//                            commentText.setText("");
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });

        clickRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   username = dataSnapshot.child("username").getValue().toString();
                   date = dataSnapshot.child("date").getValue().toString();
                   time = dataSnapshot.child("time").getValue().toString();
                   description = dataSnapshot.child("description").getValue().toString();
                   postImage = dataSnapshot.child("postimage").getValue().toString();
                   profileImage = dataSnapshot.child("profileimage").getValue().toString();
                   databaseUserId = dataSnapshot.child("uid").getValue().toString();

                   clickPostUsername.setText(username);
                   clickPostDate.setText(date);
                   clickPostTime.setText(time);
                   clickPostDescription.setText(description);
                   Picasso.get().load(postImage).into(clickPostImage);
                   Picasso.get().load(profileImage).into(clickPostProfileImg);

                   if(currentUserId.equals(databaseUserId)){
                       clickPostDelete.setVisibility(View.VISIBLE);
                       clickPostEdit.setVisibility(View.VISIBLE);
                   }
                   clickPostEdit.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           editCurrentPost(description);
                       }
                   });

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        clickPostDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);

                builder.setMessage("คุณต้องการลบใช่หรือไม่")
                        .setCancelable(false)
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCurrentPost();
                            }
                        })
                        .setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void displsyAllUsersComment(){
        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(commentRef, Comments.class)
                        .build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {
                holder.cmUsername.setText(model.getUsername());
                holder.cmDate.setText(model.getDate());
                holder.comment.setText(model.getComment());
                Picasso.get().load(model.getProfileimage()).into(holder.cmUserImg);
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_comment, viewGroup, false);
                CommentsViewHolder viewHolder = new CommentsViewHolder(view);
                return viewHolder;
            }
        };

        commentsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView cmUserImg;
        TextView cmUsername, cmDate, comment;


        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            cmUserImg = itemView.findViewById(R.id.comment_user_profile);
            cmUsername = itemView.findViewById(R.id.comment_username);
            cmDate = itemView.findViewById(R.id.comment_date);
            comment = itemView.findViewById(R.id.comment_user);
        }
    }

    private void editCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("แก้ไขกระทู้:");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("แก้ไข", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "ทำการแก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow();
    }


    private void validateComment(String userName, String userProfileImage) {
        String comment = commentText.getText().toString();

        if (TextUtils.isEmpty(comment)){
            Toast.makeText(this, "กรุณากรอกคอมเมนส์", Toast.LENGTH_SHORT).show();
        }else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            final String RandomKey = currentUserId + saveCurrentDate + saveCurrentTime;

            HashMap hashMap = new HashMap();
            hashMap.put("uid", currentUserId);
            hashMap.put("comment", comment);
            hashMap.put("date", saveCurrentDate);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("username", userName);
            hashMap.put("profileimage", userProfileImage);

            commentRef.child(RandomKey).updateChildren(hashMap);
        }
    }

    private void deleteCurrentPost() {
        clickRef.removeValue();
        SendUserToMainActivity();
        Toast.makeText(this, "กระทู้ถูกลบสำเร็จ", Toast.LENGTH_SHORT).show();


    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
