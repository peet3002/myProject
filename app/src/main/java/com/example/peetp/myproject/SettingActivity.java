package com.example.peetp.myproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.peetp.myproject.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, fullName, status, mobileNumber,uid ,degree, sec;
    private Spinner majorSpinner;
    private Button updateAccountBtn, clearBtn;
    private CircleImageView userProfileImg;

    private ProgressDialog loadingBar;

    private DatabaseReference settingsUserRef;
    private FirebaseAuth mAuth;
    private StorageReference userProfileImageRef;

    private String currentUserId, saveCurrentTime, saveCurrentDate;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ตั้งค่า");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uid = (EditText) findViewById(R.id.settings_uid);
        userName = (EditText) findViewById(R.id.settings_username);
        fullName = (EditText) findViewById(R.id.settings_fullname);
        majorSpinner = (Spinner) findViewById(R.id.settings_major_spinner);
        status = (EditText) findViewById(R.id.settings_status);
        mobileNumber = (EditText) findViewById(R.id.settings_phone);
        degree = (EditText) findViewById(R.id.setting_degree);
        sec = (EditText) findViewById(R.id.setting_sec);
        userProfileImg = (CircleImageView) findViewById(R.id.settings_profile_image);
        updateAccountBtn = (Button) findViewById(R.id.setting_update_btn);
        clearBtn = (Button) findViewById(R.id.setting_clear);

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyymmdd");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calFordTime.getTime());


        loadingBar = new ProgressDialog(this);

        loadData();

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        updateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
            }
        });

        userProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick );
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

                StorageReference filePath = userProfileImageRef.child(currentUserId +  saveCurrentDate + saveCurrentTime + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingActivity.this,"บันทึกรูปภาพเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    settingsUserRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Intent selfIntent = new Intent(SettingActivity.this, SettingActivity.class);
                                                        startActivity(selfIntent);
                                                        finish();
                                                        Toast.makeText(SettingActivity.this,"Profile image stored to firebase successfully...", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                    else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SettingActivity.this,"เกิดเหตุขัดข้อง: " +message, Toast.LENGTH_SHORT).show();
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

    private void validateAccountInfo() {
        String username = userName.getText().toString();
        String userfullname = fullName.getText().toString();
        String userstatus = status.getText().toString();
        String userid = uid.getText().toString();
        String usermobile = mobileNumber.getText().toString();
        String usermajor = majorSpinner.getSelectedItem().toString();
        String userdegree = degree.getText().toString();
        String usersec = sec.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "กรุณาชื่อโปรไฟล์", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userfullname)){
            Toast.makeText(this, "กรุณากรอกชื่อ-นามสกุล", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userid)){
            Toast.makeText(this, "กรุณากรอกรหัสนักศึกษา", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usermajor)){
            Toast.makeText(this, "กรุณาเลือกสาขา", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userdegree)){
            Toast.makeText(this, "กรุณากรอกชั้นปี", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usersec)){
            Toast.makeText(this, "กรุณากรอกเซค", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usermobile)){
            Toast.makeText(this, "กรุณากรอกเบอร์โทรศัพท์", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("กำลังบันทึกข้อมูล");
            loadingBar.setMessage("กรุณารอสักครู่กำลังบันทึกข้อมูลของคุณ...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            HashMap userMap = new HashMap();
            userMap.put("uid",userid);
            userMap.put("username", username);
            userMap.put("fullname", userfullname);
            userMap.put("major", usermajor);
            userMap.put("mobilenumber", usermobile);
            userMap.put("status", userstatus);
            userMap.put("degree", userdegree);
            userMap.put("sec", usersec);
            settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SettingActivity.this, "ช้อมูลบันทึกเเสร็จสิ้น",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(SettingActivity.this, "Error Occured: "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }


    }

    private void loadData(){
        settingsUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Users data = dataSnapshot.getValue(Users.class);
                    String myMajor = data.getMajor();

                    String[] major = getResources().getStringArray(R.array.major);
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, major);


                    Picasso.get().load(data.getProfileimage()).placeholder(R.drawable.profile).into(userProfileImg);
                    uid.setText(data.getUid());
                    userName.setText(data.getUsername());
                    fullName.setText(data.getFullname());
                    majorSpinner.setAdapter(adapter);
                    if(myMajor.equals("เคมี")){
                        majorSpinner.setSelection(1);
                    }else if(myMajor.equals("คณิตศาสตร์")){
                        majorSpinner.setSelection(2);
                    }else if(myMajor.equals("ชีววิทยา")){
                        majorSpinner.setSelection(3);
                    }else if(myMajor.equals("ฟิสิกส์")){
                        majorSpinner.setSelection(4);
                    }else if(myMajor.equals("เทคโนโลยีสารสนเทศ")){
                        majorSpinner.setSelection(5);
                    }else if(myMajor.equals("วิทยาการคอมพิวเตอร์")){
                        majorSpinner.setSelection(6);
                    }else if(myMajor.equals("สถิติประยุกต์")) {
                        majorSpinner.setSelection(7);
                    }else {
                        majorSpinner.setSelection(0);
                    }
                    status.setText(data.getStatus());
                    mobileNumber.setText(data.getMobilenumber());
                    degree.setText(data.getDegree());
                    sec.setText(data.getSec());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
