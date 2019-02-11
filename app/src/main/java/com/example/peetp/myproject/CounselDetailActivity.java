package com.example.peetp.myproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peetp.myproject.model.Comments;
import com.example.peetp.myproject.model.Counsels;
import com.example.peetp.myproject.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CounselDetailActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView counselTag, counselDetail;
    private EditText commentText;
    private ImageButton deleteBtn;
    private CheckBox counselCheckbox;
    private Button saveBtn, cancelBtn;
    private Menu option_Menu;

    private FirebaseAuth mAuth;
    private DatabaseReference counselRef, usersRef, commentRef, counselAllRef;

    private RecyclerView commentsList;


    private String PostKey, current_user_id , receiver_status, counsel_key , str1,str2, advicer;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsel_detail);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostKey = getIntent().getExtras().get("PostKey").toString();
        advicer = getIntent().getExtras().get("Adviser").toString();

        counselRef = FirebaseDatabase.getInstance().getReference().child("Counsels");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Counsels").child(advicer).child(PostKey).child("Comments");

        counselTag = (TextView) findViewById(R.id.counsel_detail_tag);
        counselDetail = (TextView) findViewById(R.id.counsel_detail_view);
        commentText = (EditText) findViewById(R.id.counsel_detail_edit);
        counselCheckbox = (CheckBox) findViewById(R.id.counsel_detail_chkbox);
        saveBtn = (Button) findViewById(R.id.counsel_detail_save);
        cancelBtn = (Button) findViewById(R.id.counsel_detail_cancel);
        deleteBtn = (ImageButton) findViewById(R.id.counsel_detail_delete);

        mToolbar = (Toolbar) findViewById(R.id.counsel_detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("บันทึกการให้คำปรึกษา");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        commentsList = (RecyclerView) findViewById(R.id.counsel_comment_list);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        commentsList.setLayoutManager(linearLayoutManager);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);


        setCounselCheckbox();
        setCounselCheckboxEvent();
        displayAllComment();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Users users = dataSnapshot.getValue(Users.class);
                            String userName = users.getUsername();
                            validateComment(userName);
                            commentText.setText("");
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentText.setText("");
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CounselDetailActivity.this,R.style.AlertDialog);

                builder.setMessage("คุณต้องการลบใช่หรือไม่")
                        .setCancelable(false)
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                counselRef.child(advicer).child(PostKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(CounselDetailActivity.this, "ทำการลบสำเร็จ", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                });
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
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });


        commentText.requestFocus();
        final ScrollView main = (ScrollView) findViewById(R.id.scrollView);

        main.post(new Runnable() {
            @Override
            public void run() {
                main.scrollTo(0,0);
            }
        });

    }

    private void setCounselCheckbox(){
        counselRef.child(advicer).child(PostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists()){
                  Counsels counsels = dataSnapshot.getValue(Counsels.class);
                  counsel_key = counsels.getKey();
                  receiver_status = counsels.getReceiver_status();

                  counselTag.setText(counsels.getType());
                  counselDetail.setText(counsels.getDetail());

                  if(counsel_key.equals(current_user_id)){
                      counselCheckbox.setVisibility(View.VISIBLE);
                      deleteBtn.setVisibility(View.VISIBLE);

                      if(counsels.getStatus() == true){
                          counselCheckbox.setChecked(true);
                      }else{
                          counselCheckbox.setChecked(false);
                      }


                  }else {
                      counselCheckbox.setVisibility(View.GONE);
                      deleteBtn.setVisibility(View.GONE);

                  }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCounselCheckboxEvent(){
        counselCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counselCheckbox.isChecked()){
                    str1 = receiver_status.replace("false", "true");
                    HashMap hashMap = new HashMap();
                    hashMap.put("status",true);
                    hashMap.put("receiver_status", str1);
                    counselRef.child(advicer).child(PostKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(CounselDetailActivity.this, "บันทึกการให้คำปรึกษา: ได้รับการแก้ไขแล้ว",Toast.LENGTH_LONG).show();
                        }
                    });


                }else if(!counselCheckbox.isChecked()) {
                    str2 = receiver_status.replace("true", "false");
                    HashMap userMap = new HashMap();
                    userMap.put("status",false);
                    userMap.put("receiver_status", str2);
                    counselRef.child(advicer).child(PostKey).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(CounselDetailActivity.this, "บันทึกการให้คำปรึกษา: ยังไม่ได้รับการแก้ไข",Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }


    private void displayAllComment() {
        Query sortPost = commentRef.orderByChild("date_time");
        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(sortPost, Comments.class)
                        .build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {
                holder.cmUsername.setText(model.getUsername());
                holder.cmDate.setText(model.getDate());
                holder.cmTime.setText(model.getTime());
                holder.comment.setText(model.getComment());
            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_holder_counsel_comment_you, viewGroup, false);
                CommentsViewHolder viewHolder = new CommentsViewHolder(view);
                return viewHolder;
            }
        };

        commentsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        TextView cmUsername, cmDate, cmTime, comment;


        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            cmUsername = itemView.findViewById(R.id.counsel_comment_username);
            cmDate = itemView.findViewById(R.id.counsel_comment_date);
            cmTime = itemView.findViewById(R.id.counsel_comment_time);
            comment = itemView.findViewById(R.id.counsel_comment_detail);

        }
    }

    private void validateComment(String userName) {
        String comment = commentText.getText().toString();
        if (TextUtils.isEmpty(comment)){
            Toast.makeText(this, "กรุณากรอกคอมเมนส์", Toast.LENGTH_SHORT).show();
        }else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordYear = Calendar.getInstance();
            SimpleDateFormat currentYear = new SimpleDateFormat("yyyy",new Locale("th"));
            final String saveCurrentYear = currentYear.format(calFordYear.getTime());
            year = Integer.parseInt(saveCurrentYear);
            year += 543;

            Calendar calFordYearDate = Calendar.getInstance();
            SimpleDateFormat currentYearDate = new SimpleDateFormat("yyyyMMdd");
            final String saveCurrentYearDate = currentYearDate.format(calFordYearDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            Calendar calFordTime2 = Calendar.getInstance();
            SimpleDateFormat currentTime2 = new SimpleDateFormat("HHmmss");
            final String saveCurrentTime2 = currentTime2.format(calFordDate.getTime());

            final String RandomKey = current_user_id + year + saveCurrentDate + saveCurrentTime;

            HashMap hashMap = new HashMap();
            hashMap.put("key", current_user_id);
            hashMap.put("comment", comment);
            hashMap.put("date", saveCurrentDate + "-" +year);
            hashMap.put("date_time",saveCurrentYearDate + saveCurrentTime2);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("username", userName);
            commentRef.child(RandomKey).updateChildren(hashMap);
        }
    }
}
