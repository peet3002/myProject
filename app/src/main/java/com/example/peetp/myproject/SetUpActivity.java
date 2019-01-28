package com.example.peetp.myproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {

    private EditText fullname, name, major, mobileNumber, id, degree, sec, adviser;

    private Button saveInformationbutton, clearButton;
    private CircleImageView profileImage;
    private ProgressDialog loadingBar;
    private ImageButton search;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, usersRef, stdRef, teacherRef, adviserRef;
    private StorageReference userProfileImageRef;


    private int textlength = 0, year;
    private String saveCurrentDate, saveCurrentYear ,saveCurrentTime, saveCurrentDateFull;

    String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        teacherRef = FirebaseDatabase.getInstance().getReference().child("Teacher");
        adviserRef = FirebaseDatabase.getInstance().getReference().child("Adviser").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        id = (EditText) findViewById(R.id.setup_id);
        search = (ImageButton) findViewById(R.id.setup_search);
        fullname = (EditText) findViewById(R.id.setup_fullname);
        name = (EditText) findViewById(R.id.setup_name);
        major = (EditText) findViewById(R.id.setup_major);
        mobileNumber = (EditText) findViewById(R.id.setup_mobile);
        degree = (EditText) findViewById(R.id.setup_degree);
        sec = (EditText) findViewById(R.id.setup_sec);
        adviser = (EditText) findViewById(R.id.setup_adviser);

        saveInformationbutton = (Button) findViewById(R.id.setup_save_btn);
        clearButton = (Button) findViewById(R.id.setup_clear);
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_image);

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyymmdd");
        saveCurrentDateFull = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        String simple = "ชื่อโปรไฟล์";
        String colored = "*";
        SpannableStringBuilder builder = new SpannableStringBuilder();


        builder.append(simple);
        int start = builder.length();
        builder.append(colored);
        int end = builder.length();

        builder.setSpan(new ForegroundColorSpan(Color.RED), start , end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        name.setHint(builder);

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = id.getText().toString();
                textlength = id.getText().length();

                if(textlength == 13){
                    if(!text.contains("-")){
                        id.setText(new StringBuilder(text).insert(text.length() - 1, "-").toString());
                        id.setSelection(id.getText().length());
                    }
                    }
                }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtSearch = id.getText().toString();
                String txtMajor = major.getText().toString();
                String txtDegree = degree.getText().toString();
                String txtSec = sec.getText().toString();

                if (!TextUtils.isEmpty(txtSearch)) {
                    stdRef = FirebaseDatabase.getInstance().getReference().child("Students").child(txtSearch);
                    stdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String userFullName = dataSnapshot.child("fullname").getValue().toString();
                                String userMajor = dataSnapshot.child("major").getValue().toString();
                                String userDegree = dataSnapshot.child("degree").getValue().toString();
                                String userSec = dataSnapshot.child("sec").getValue().toString();

                                major.setText(userMajor);
                                fullname.setText(userFullName);
                                degree.setText(userDegree);
                                sec.setText(userSec);

                                teacherRef.child(userMajor).child(userDegree).child(userSec)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String userTeacher = dataSnapshot.child("fullname").getValue().toString();

                                                adviser.setText(userTeacher);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                            }
                            else{
                                Toast.makeText(SetUpActivity.this, "ไม่พบข้อมูลรหัสนักศีกษา",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });



        loadingBar = new ProgressDialog(this);

        saveInformationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountSetupInformation();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.getText().clear();
                fullname.getText().clear();
                id.getText().clear();
                major.getText().clear();
                degree.getText().clear();
                sec.getText().clear();
                adviser.getText().clear();
                mobileNumber.getText().clear();

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

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);
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

                    StorageReference filePath = userProfileImageRef.child(currentUserId + saveCurrentDateFull + saveCurrentTime + ".jpg");

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
        String userDegree = degree.getText().toString();
        String userSec = sec.getText().toString();
        String userAdviser = adviser.getText().toString();

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordYear = Calendar.getInstance();
        SimpleDateFormat currentYear = new SimpleDateFormat("yyyy",new Locale("th"));
        saveCurrentYear = currentYear.format(calFordYear.getTime());
        year = Integer.parseInt(saveCurrentYear);
        year += 543;


        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "กรุณาชื่อโปรไฟล์", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userId)){
            Toast.makeText(this, "กรุณากรอกรหัสนักศึกษา", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userFullName)){
            Toast.makeText(this, "กรุณากรอกชื่อ-นามสกุล", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userMajor)){
            Toast.makeText(this, "กรุณากรอกสาขา", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userMobileNumber)){
            Toast.makeText(this, "กรุณากรอกเบอร์โทรศัพท์", Toast.LENGTH_SHORT).show();
        }

        else{
            loadingBar.setTitle("กำลังบันทึกข้อมูล");
            loadingBar.setMessage("กรุณารอสักครู่กำลังบันทึกข้อมูลของคุณ...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            final HashMap userMap = new HashMap();
            userMap.put("uid",userId);
            userMap.put("username", userName);
            userMap.put("fullname", userFullName);
            userMap.put("major", userMajor);
            userMap.put("mobilenumber", userMobileNumber);
            userMap.put("degree",userDegree);
            userMap.put("sec", userSec);
            userMap.put("key",currentUserId);
            userMap.put("adviser", userAdviser);
            userMap.put("status", "Hey there i am using Test");
            userMap.put("usertype","student");

            final HashMap adviserMap = new HashMap();
            adviserMap.put("date", saveCurrentDate + "-" +year);



            usersRef.orderByChild("fullname").equalTo(userAdviser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String id  = dataSnapshot.getKey();
                        String dataKeys = "";

                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            //Object object = child.getKey();

                            dataKeys=dataKeys+child.getKey() + "";
                        }
                        adviserRef.child(dataKeys).updateChildren(adviserMap);
//                        Toast.makeText(SetUpActivity.this, dataKeys,Toast.LENGTH_SHORT).show();
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
                    else{
                        Toast.makeText(SetUpActivity.this, "ข้ออภัย: อาจารย์ที่ปรึกษาของท่านไม่มีชื่ออยู่ในระบบ", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
