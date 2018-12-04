package com.example.peetp.myproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private EditText fullname, name, major, mobileNumber, id, email;

    private Button saveInformationbutton;
    private CircleImageView profileImage;
    private ProgressDialog loadingBar;
    private ImageButton search;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, stdRef;
    private StorageReference userProfileImageRef;

    String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        id = (EditText) findViewById(R.id.setup_id);
        search = (ImageButton) findViewById(R.id.setup_search);
        fullname = (EditText) findViewById(R.id.setup_fullname);
        name = (EditText) findViewById(R.id.setup_name);
        major = (EditText) findViewById(R.id.setup_major);
        mobileNumber = (EditText) findViewById(R.id.setup_mobile);
        email = (EditText) findViewById(R.id.setup_email);
        saveInformationbutton = (Button) findViewById(R.id.setup_save_btn);
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_image);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtSearch = id.getText().toString();
                stdRef = FirebaseDatabase.getInstance().getReference().child("Students").child(txtSearch);
                stdRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String userFullName = dataSnapshot.child("fullname").getValue().toString();
                            String userMajor = dataSnapshot.child("major").getValue().toString();

                            major.setText(userMajor);
                            fullname.setText(userFullName);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



        loadingBar = new ProgressDialog(this);

        saveInformationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountSetupInformation();
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick );
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);
                    }
                    else{
                        Toast.makeText(SetUpActivity.this, "Please select Profile image first",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode==Gallery_Pick && resultCode == RESULT_OK && data != null){
                Uri ImageUri = data.getData();

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            }
            if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if(resultCode == RESULT_OK){
                    loadingBar.setTitle("Profile Image");
                    loadingBar.setMessage("กรุณารอสักครู่กำลังอัพเดทรูปภาพของคุณ...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();


                    Uri resultUri = result.getUri();

                    StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SetUpActivity.this,"บันทึกรูปภาพเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        userRef.child("profileimage").setValue(downloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Intent selfIntent = new Intent(SetUpActivity.this, SetUpActivity.class);
                                                            startActivity(selfIntent);

                                                            Toast.makeText(SetUpActivity.this,"Profile image stored to firebase successfully...", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();

                                                        }
                                                        else {
                                                            String message = task.getException().getMessage();
                                                            Toast.makeText(SetUpActivity.this,"Error Occured: " +message, Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        }

                                                    }
                                                });

                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(this,"Error Occured: Image can be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
    }



    private void saveAccountSetupInformation() {
        String userName = name.getText().toString();
        String userFullName = fullname.getText().toString();
        String userMajor = major.getText().toString();
        String userMobileNumber = mobileNumber.getText().toString();
        String userId = id.getText().toString();
        String userEmail = email.getText().toString();

        if(TextUtils.isEmpty(userId)){
            Toast.makeText(this, "กรุณากรอกรหัสนักศึกษา", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userFullName)){
            Toast.makeText(this, "กรุณากรอกชื่อ-นามสกุล", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "กรุณาชื่อโปรไฟล์", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userMajor)){
            Toast.makeText(this, "กรุณากรอกสาขา", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userMobileNumber)){
            Toast.makeText(this, "กรุณากรอกเบอร์โทรศัพท์", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "กรุณากรอกอีเมล์", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("กำลังบันทึกข้อมูล");
            loadingBar.setMessage("กรุณารอสักครู่กำลังบันทึกข้อมูลของคุณ...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("uid",userId);
            userMap.put("username", userName);
            userMap.put("fullname", userFullName);
            userMap.put("major", userMajor);
            userMap.put("mobilenumber", userMobileNumber);
            userMap.put("email",  userEmail);
            userMap.put("status", "Hey there i am using Test");
            userMap.put("usertype","student");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SetUpActivity.this, "ช้อมูลบันทึกเเสร็จสิ้น",Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(SetUpActivity.this, "Error Occured: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetUpActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
