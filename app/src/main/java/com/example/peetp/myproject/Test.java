package com.example.peetp.myproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Test extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton;

    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        databaseReference = FirebaseDatabase.getInstance().getReference("Students");

        editText = (EditText) findViewById(R.id.editText);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        recyclerView = (RecyclerView) findViewById(R.id.rv);

//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseUserSearch();
//            }
//        });
    }



//    private void firebaseUserSearch() {
//        FirebaseRecyclerAdapter<Students, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Students, UserViewHolder>() {
//            @Override
//            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Students model) {
//                Students.class,
//                R.layout.list,
//                UserViewHolder.class,
//
//            }
//
//            @NonNull
//            @Override
//            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                return null;
//            }
//        };
//
//    }

//    public class UserViewHolder extends RecyclerView.ViewHolder{
//
//        View mView;
//
//        public UserViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            mView = itemView;
//        }
//        public void setDetail(String userName, String useMajor, String userUid){
//            TextView user_name = (TextView) mView.findViewById(R.id.name_text);
//            TextView user_major = (TextView) mView.findViewById(R.id.major_text);
//            TextView user_uid = (TextView) mView.findViewById(R.id.uid_text);
//
//        }
//    }
}
