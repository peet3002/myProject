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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.peetp.myproject.model.Posts;
import com.example.peetp.myproject.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private ImageButton selectPostImage;
    private Button updatePostButton;
    private EditText postDescription, postHeader;
    private Spinner postType;
    private static final  int Gallery_Pick = 1;
    private Uri imageUri;
    private String description, header, type;

    private StorageReference postImagesRefrence;
    private DatabaseReference usersRef , postRef;
    private FirebaseAuth mAuth;
    private Users users;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id, saveCurrentYear;
    private int year;
    private long countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        postImagesRefrence = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        selectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        updatePostButton = (Button) findViewById(R.id.update_post_button);
        postHeader = (EditText) findViewById(R.id.post_header);
        postType = (Spinner) findViewById(R.id.post_type);

        postDescription = (EditText) findViewById(R.id.post_description);
        loadingBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.update_post_page);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("เขียนกระทู้ใหม่");

        String[] postTypeStrings = getResources().getStringArray(R.array.postType);
        ArrayAdapter<String> adapterPostType = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, postTypeStrings);
        postType.setAdapter(adapterPostType);

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePostInfo();
            }
        });
    }

    private void validatePostInfo() {
        header = postHeader.getText().toString();
        type = postType.getSelectedItem().toString();
        description = postDescription.getText().toString();

        if (TextUtils.isEmpty(header)){
            Toast.makeText(this, "กรุณากรอกชื่อเรื่อง",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(type)){
            Toast.makeText(this, "กรุณาเลือกหมวดหมู่",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "กรุณากรอกข้อความ",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("เพิ่มกระทู้");
            loadingBar.setMessage("กรุณารอสักครู่กำลังสร้างกระทุ้ใหม่...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            storingImageToFirebaseStorage();
        }
    }

    private void storingImageToFirebaseStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordYear = Calendar.getInstance();
        SimpleDateFormat currentYear = new SimpleDateFormat("yyyy",new Locale("th"));
        saveCurrentYear = currentYear.format(calFordYear.getTime());
        year = Integer.parseInt(saveCurrentYear);
        year += 543;

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = year + saveCurrentDate + saveCurrentTime;

        if(imageUri != null) {
            final StorageReference filePath = postImagesRefrence.child("Post Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");
            filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downUri = task.getResult();
                                downloadUrl = downUri.toString();
                                SavingPostInformationToDatabase();
                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(PostActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            SavingPostInformationToDatabase();
        }


        }


    private void SavingPostInformationToDatabase() {
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    countPosts = dataSnapshot.getChildrenCount();
                }
                else{
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    users = dataSnapshot.getValue(Users.class);

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate + "-" +year);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("postheader", header);
                    postsMap.put("posttype", type);
                    postsMap.put("description", description);
                    if(imageUri != null){
                        postsMap.put("postimage", downloadUrl);
                    }
                    postsMap.put("profileimage", users.getProfileimage());
                    postsMap.put("username", users.getUsername());
                    postsMap.put("counter", countPosts);

                    postRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        sendUserToMainActivity();
                                        Toast.makeText(PostActivity.this, "กระทู้ของคุณถูกสร้างเรียบร้อยแล้ว.",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else{

                                        Toast.makeText(PostActivity.this, "เกิดเหตุขัดข้อง: ขณะกำลังสร้างกระทู้.  ",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick );

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left , R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK  && data != null){
            imageUri = data.getData();
            selectPostImage.setImageURI(imageUri);
        }else
        {
            imageUri = null;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }



}
