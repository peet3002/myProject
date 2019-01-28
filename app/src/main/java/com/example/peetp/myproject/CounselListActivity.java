package com.example.peetp.myproject;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.peetp.myproject.model.Counsels;
import com.example.peetp.myproject.model.Posts;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CounselListActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private RecyclerView counselList;

    private DatabaseReference counselRef, usersRef;
    private FirebaseAuth mAuth;
    private String current_user_id, advisor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsel_list);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        counselRef = FirebaseDatabase.getInstance().getReference().child("Counsels");

        mToolbar = (Toolbar) findViewById(R.id.counsel_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("บันทึกการให้คำปรึกษา");

        counselList = (RecyclerView) findViewById(R.id.counsel_list);
        counselList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        counselList.setLayoutManager(linearLayoutManager);
        usersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users users = dataSnapshot.getValue(Users.class);
                    advisor = users.getAdviser();
                    DisplsyUsersCounsel();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ic_plus:
                sendUserToCounselActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void DisplsyUsersCounsel() {

        Query sortPost = counselRef.child(advisor).orderByChild("key").equalTo(current_user_id);
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

                        holder.counselDetail.setText(model.getDetail());
                        holder.counselDate.setText(model.getDate());
                        holder.counselType.setText(model.getType());

                        counselRef.child(advisor).child(PostKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Counsels counsels = dataSnapshot.getValue(Counsels.class);

                                    if(counsels.getStatus() == true){
                                        holder.verifiedIc.setVisibility(View.VISIBLE);
                                        holder.errorIc.setVisibility(View.GONE);
                                    }else{
                                        holder.verifiedIc.setVisibility(View.GONE);
                                        holder.errorIc.setVisibility(View.VISIBLE);
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent detailIntent = new Intent(CounselListActivity.this, CounselDetailActivity.class);
                                            detailIntent.putExtra("PostKey",PostKey);
                                            detailIntent.putExtra("Adviser",advisor);
                                            startActivity(detailIntent);
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public CounselViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.counsel_item, viewGroup , false);
                        CounselViewHolder viewHolder = new CounselViewHolder(view);
                        return viewHolder;
                    }
                };
        counselList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class CounselViewHolder extends RecyclerView.ViewHolder{
        ImageView verifiedIc, errorIc;
        TextView counselDetail, counselDate, counselType;

        public CounselViewHolder(@NonNull View itemView) {
            super(itemView);

            counselDetail = itemView.findViewById(R.id.counsel_detail_item);
            counselDate = itemView.findViewById(R.id.counsel_date_item);
            counselType = itemView.findViewById(R.id.counsel_tag_item);
            verifiedIc = itemView.findViewById(R.id.verified_ic_item);
            errorIc = itemView.findViewById(R.id.error_ic_item);

        }
    }

    private void sendUserToCounselActivity() {
        Intent intent = new Intent(CounselListActivity.this, CounselActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
