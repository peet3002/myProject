package com.example.peetp.myproject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.peetp.myproject.model.Comments;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private EditText commentText;
    private ImageButton postCommentBtn;
    private RecyclerView commentsList;

    private DatabaseReference usersRef, postsRef;
    private FirebaseAuth mAuth;

    private int year;
    private String Post_Key, current_user_id, userName, comment, userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");

        Toolbar toolbar = (Toolbar) findViewById(R.id.cm_toolbar);
        commentText = (EditText) findViewById(R.id.cm_add_comment);
        postCommentBtn = (ImageButton) findViewById(R.id.cm_post);


        commentsList = (RecyclerView) findViewById(R.id.cm_recycler_view);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        commentsList.setLayoutManager(linearLayoutManager);

        displayAllComment();

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEvents();
            }
        });
    }

    private void displayAllComment(){
        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(postsRef, Comments.class)
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

    private void commentEvents() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        usersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userName = dataSnapshot.child("username").getValue().toString();
                    userProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    validateComments(userName, userProfileImage);
                    commentText.setText("");
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validateComments(String userName, String userProfileImage) {
        comment = commentText.getText().toString();

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "กรุณากรอกคอมเมนส์", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordYear = Calendar.getInstance();
            SimpleDateFormat currentYear = new SimpleDateFormat("yyyy",new Locale("th"));
            final String saveCurrentYear = currentYear.format(calFordYear.getTime());
            year = Integer.parseInt(saveCurrentYear);
            year += 543;

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());


            final String RandomKey = current_user_id + year + saveCurrentDate + saveCurrentTime;



            HashMap hashMap = new HashMap();
            hashMap.put("uid", current_user_id);
            hashMap.put("comment", comment);
            hashMap.put("date", saveCurrentDate + "-" +year);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("username", userName);
            hashMap.put("profileimage", userProfileImage);

            postsRef.child(RandomKey).updateChildren(hashMap);
        }
    }
}



