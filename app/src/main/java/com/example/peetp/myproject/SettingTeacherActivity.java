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
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingTeacherActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName, userFullname, userStatus ,userMobilePhone, userOffice;
    private Spinner majorSpinner;
    private Button updateAccountBtn, clearBtn;
    private CircleImageView userProfileImg;

    private ProgressDialog loadingBar;

    private DatabaseReference settingsUserRef;
    private FirebaseAuth mAuth;
    private StorageReference userProfileImageRef;

    private String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_teacher);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        IntializeFields();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ตั้งค่า");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

                StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingTeacherActivity.this,"บันทึกรูปภาพเสร็จสิ้น", Toast.LENGTH_SHORT).show();
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
                                                        Intent selfIntent = new Intent(SettingTeacherActivity.this, SettingTeacherActivity.class);
                                                        startActivity(selfIntent);
                                                        finish();

                                                        Toast.makeText(SettingTeacherActivity.this,"Profile image stored to firebase successfully...", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                    else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SettingTeacherActivity.this,"เกิดเหตุขัดข้อง: " +message, Toast.LENGTH_SHORT).show();
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
        String userfullname = userFullname.getText().toString();
        String userstatus = userStatus.getText().toString();
        String usermobile = userMobilePhone.getText().toString();
        String usermajor = majorSpinner.getSelectedItem().toString();
        String useroffice = userOffice.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "กรุณาชื่อโปรไฟล์", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userfullname)){
            Toast.makeText(this, "กรุณากรอกชื่อ-นามสกุล", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usermajor)){
            Toast.makeText(this, "กรุณาเลือกสาขา", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(usermobile)){
            Toast.makeText(this, "กรุณากรอกเบอร์โทรศัพท์", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(useroffice)){
            Toast.makeText(this, "กรุณากรอกโต๊ะทำงาน", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("กำลังบันทึกข้อมูล");
            loadingBar.setMessage("กรุณารอสักครู่กำลังบันทึกข้อมูลของคุณ...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            updateAccountInfo(username, userfullname, userstatus, usermobile, usermajor,  useroffice );
        }

    }

    private void updateAccountInfo(String username, String userfullname, String userstatus, String usermobile, String usermajor, String useroffice) {
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullname", userfullname);
        userMap.put("major", usermajor);
        userMap.put("mobilenumber", usermobile);
        userMap.put("status", userstatus);
        userMap.put("office", useroffice);
        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    sendUserToProfileActivity();
                    Toast.makeText(SettingTeacherActivity.this, "ช้อมูลบันทึกเเสร็จสิ้น",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
                else{
                    String message = task.getException().getMessage();
                    Toast.makeText(SettingTeacherActivity.this, "Error Occured: "+message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void loadData() {
        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myFullName = dataSnapshot.child("fullname").getValue().toString();
                    String myMajor = dataSnapshot.child("major").getValue().toString();
                    String myMobileNumber = dataSnapshot.child("mobilenumber").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myOffice = dataSnapshot.child("office").getValue().toString();

                    String[] major = getResources().getStringArray(R.array.major);
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingTeacherActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, major);

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImg);
                    userName.setText(myUserName);
                    userFullname.setText(myFullName);
                    userStatus.setText(myStatus);
                    userMobilePhone.setText(myMobileNumber);
                    userOffice.setText(myOffice);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IntializeFields() {
        mToolbar = (Toolbar) findViewById(R.id.teacher_settings_toolbar);
        userName = (EditText) findViewById(R.id.teacher_settings_username);
        userFullname = (EditText) findViewById(R.id.teacher_settings_fullname);
        userStatus = (EditText) findViewById(R.id.teacher_settings_status);
        majorSpinner = (Spinner) findViewById(R.id.teacher_settings_major_spinner);
        userMobilePhone = (EditText) findViewById(R.id.teacher_settings_phone);
        userProfileImg = (CircleImageView) findViewById(R.id.teacher_settings_profile_image);
        userOffice = (EditText) findViewById(R.id.teacher_settings_office);
        updateAccountBtn = (Button) findViewById(R.id.teacher_settings_update_btn);
        clearBtn = (Button) findViewById(R.id.teacher_settings_clear);

    }
    private void sendUserToProfileActivity(){
        Intent profileIntent = new Intent(SettingTeacherActivity.this, TeacherProfileActivity.class);
        startActivity(profileIntent);
        finish();
    }
}
