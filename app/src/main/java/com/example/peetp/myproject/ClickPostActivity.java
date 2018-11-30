package com.example.peetp.myproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ImageView clickPostImage;
    private TextView clickPostDescription;
    private ImageButton clickPostEdit, clickPostDelete;

    private DatabaseReference clickRef;
    private FirebaseAuth mAuth;

    private String PostKey, currentUserId, databaseUserId, description, image;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        clickRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        clickPostImage = (ImageView) findViewById(R.id.click_post_image);
        clickPostDescription = (TextView) findViewById(R.id.click_post_description);
        clickPostEdit = (ImageButton) findViewById(R.id.click_post_edit);
        clickPostDelete = (ImageButton) findViewById(R.id.click_post_delete);

        clickPostDelete.setVisibility(View.INVISIBLE);
        clickPostEdit.setVisibility(View.INVISIBLE);

        clickRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   description = dataSnapshot.child("description").getValue().toString();
                   image = dataSnapshot.child("postimage").getValue().toString();
                   databaseUserId = dataSnapshot.child("uid").getValue().toString();

                   clickPostDescription.setText(description);
                   Picasso.get().load(image).into(clickPostImage);

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
