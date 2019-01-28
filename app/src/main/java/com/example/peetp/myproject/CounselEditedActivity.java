package com.example.peetp.myproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.peetp.myproject.model.Counsels;
import com.example.peetp.myproject.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CounselEditedActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView counselList;

    private DatabaseReference counselRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id, current_user_fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsel_edited);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        counselRef = FirebaseDatabase.getInstance().getReference().child("Counsels");

        mToolbar = (Toolbar) findViewById(R.id.counsel_edited_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("บันทึกการให้คำปรึกษา");

        counselList = (RecyclerView) findViewById(R.id.counsel_edited_list);
        counselList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        counselList.setLayoutManager(linearLayoutManager);

        usersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users users = dataSnapshot.getValue(Users.class);
                    current_user_fullname = users.getFullname();
                    DisplsyUsersCounsel();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplsyUsersCounsel() {
        Query sortPost = counselRef.child(current_user_fullname).orderByChild("receiver_status").startAt("true_" + "00000000000000").endAt("true_"+"99999999999999"+"\uf8ff");
        adapter(sortPost);
    }

    private void adapter(Query sortPost){
        FirebaseRecyclerOptions<Counsels> options =
                new FirebaseRecyclerOptions.Builder<Counsels>()
                        .setQuery(sortPost, Counsels.class)
                        .build();


        FirebaseRecyclerAdapter<Counsels, CounselViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Counsels, CounselViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final CounselViewHolder holder, int position, @NonNull Counsels model) {
                        final String PostKey = getRef(position).getKey();

                        holder.counselUid.setText(model.getUid());
                        holder.counselFullname.setText(model.getFullname());
                        holder.counselDate.setText(model.getDate());
                        holder.counselDegree.setText(model.getDegree());
                        holder.counselSec.setText(model.getSec());
                        holder.counselDetail.setText(model.getDetail());


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent detailIntent = new Intent(CounselEditedActivity.this, CounselDetailActivity.class);
                                detailIntent.putExtra("PostKey",PostKey);
                                detailIntent.putExtra("Adviser",current_user_fullname);
                                startActivity(detailIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CounselViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.counsel_teacher_item, viewGroup , false);
                        CounselViewHolder viewHolder = new CounselViewHolder(view);
                        return viewHolder;
                    }
                };
        counselList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public static class CounselViewHolder extends RecyclerView.ViewHolder{
        TextView counselUid, counselDate, counselFullname, counselDegree, counselSec, counselDetail;

        public CounselViewHolder(@NonNull View itemView) {
            super(itemView);
            counselUid = itemView.findViewById(R.id.counsel_not_uid);
            counselFullname = itemView.findViewById(R.id.counsel_not_fullname);
            counselDate = itemView.findViewById(R.id.counsel_not_date);
            counselDegree = itemView.findViewById(R.id.counsel_not_degree);
            counselSec = itemView.findViewById(R.id.counsel_not_sec);
            counselDetail = itemView.findViewById(R.id.counsel_not_detail);
        }
    }

}
