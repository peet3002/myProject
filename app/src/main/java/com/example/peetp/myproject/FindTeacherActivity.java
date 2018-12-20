package com.example.peetp.myproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.peetp.myproject.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindTeacherActivity extends AppCompatActivity {

    private EditText searchTeacher;
    private ImageButton searchBtn;

    private RecyclerView searchList;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_teacher);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchTeacher = (EditText) findViewById(R.id.find_search);
        searchBtn = (ImageButton) findViewById(R.id.find_btn);
        searchList = (RecyclerView) findViewById(R.id.find_list);
        searchList.setHasFixedSize(true);
        searchList.setLayoutManager(new LinearLayoutManager(this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchTeacher.getText().toString();

                firebaseUserSearch(searchText);
            }
        });

    }

    private void firebaseUserSearch(String searchText) {

        Query fQuery3 = userRef.orderByChild("type_username").startAt("teacher_" + searchText).endAt("teacher_" + searchText + "\uf8ff");


        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(fQuery3, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, final int position, @NonNull Users model) {
                holder.findUsername.setText(model.getUsername());
                holder.findStatus.setText(model.getStatus());
                Picasso.get().load(model.getProfileimage()).into(holder.findUserImg);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id =  getRef(position).getKey();

                        Intent profileIntent = new Intent(FindTeacherActivity.this, PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup, false);
                UsersViewHolder viewHolder = new UsersViewHolder(view);
                return viewHolder;
            }
        };

        searchList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public class UsersViewHolder extends RecyclerView.ViewHolder{
        CircleImageView findUserImg;
        TextView findUsername,findStatus;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            findUserImg = itemView.findViewById(R.id.find_img_profile);
            findUsername = itemView.findViewById(R.id.find_user_name);
            findStatus = itemView.findViewById(R.id.find_user_status);
        }
    }
}
