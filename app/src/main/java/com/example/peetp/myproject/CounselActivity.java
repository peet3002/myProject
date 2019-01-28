package com.example.peetp.myproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.peetp.myproject.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class CounselActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private EditText counselDetail;
    private Spinner counselType;
    private Button createCounsel, resetCounsel;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, counselRef;

    private String saveCurrentDate, saveCurrentTime, counselRandomName, current_user_id, saveCurrentYearDate,  saveCurrentYearMonth, saveCurrentYear;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsel);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        counselRef = FirebaseDatabase.getInstance().getReference().child("Counsels");

        mToolbar = (Toolbar) findViewById(R.id.counsel_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("บันทึกการให้คำปรึกษา");

        loadingBar = new ProgressDialog(this);

        counselDetail = (EditText) findViewById(R.id.counsel_detail);

        counselType = (Spinner) findViewById(R.id.counsel_type);
        String[] postTypeStrings = getResources().getStringArray(R.array.postType);
        ArrayAdapter<String> adapterPostType = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, postTypeStrings);
        counselType.setAdapter(adapterPostType);

        createCounsel = (Button) findViewById(R.id.create_button);
        createCounsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCounselInfo();
            }
        });
        resetCounsel = (Button) findViewById(R.id.reset_button);
        resetCounsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void validateCounselInfo() {
        String type = counselType.getSelectedItem().toString();
        String detail = counselDetail.getText().toString();

        if (TextUtils.isEmpty(type)){
            Toast.makeText(this, "กรุณาเลือกหมวดหมู่",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(detail)){
            Toast.makeText(this, "กรุณากรอกรายละเอียด",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("เพิ่มกระทู้");
            loadingBar.setMessage("กรุณารอสักครู่กำลังสร้างกระทุ้ใหม่...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            savingCounselInformationToDatabase(type , detail);
        }
    }

    private void savingCounselInformationToDatabase(final String type, final String detail) {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM",new Locale("th"));
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordYear = Calendar.getInstance();
        SimpleDateFormat currentYear = new SimpleDateFormat("yyyy",new Locale("th"));
        saveCurrentYear = currentYear.format(calFordYear.getTime());
        year = Integer.parseInt(saveCurrentYear);
        year += 543;

        Calendar calFordYearDate = Calendar.getInstance(new Locale("th"));
        SimpleDateFormat currentYearDate = new SimpleDateFormat("MMdd",new Locale("th"));
        saveCurrentYearDate = currentYearDate.format(calFordYearDate.getTime());

        Calendar calFordYearMonth = Calendar.getInstance(new Locale("th"));
        SimpleDateFormat currentYearMonth = new SimpleDateFormat("MM",new Locale("th"));
        saveCurrentYearMonth = currentYearMonth.format(calFordYearMonth.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        counselRandomName = year +saveCurrentYearDate + saveCurrentTime;

        usersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users data = dataSnapshot.getValue(Users.class);

                HashMap hashMap = new HashMap();
                hashMap.put("key", current_user_id);
                hashMap.put("date", saveCurrentDate + "-" + year);
                hashMap.put("receiver_status", "false_" +year +saveCurrentYearDate +saveCurrentTime);
                hashMap.put("time", saveCurrentTime);
                hashMap.put("type", type);
                hashMap.put("detail", detail);
                hashMap.put("username", data.getUsername());
                hashMap.put("fullname",data.getFullname());
                hashMap.put("uid",data.getUid());
                hashMap.put("degree", data.getDegree());
                hashMap.put("sec", data.getSec());
                hashMap.put("receiver", data.getAdviser());
                hashMap.put("status",false);
                hashMap.put("type_date",type +"_"+year+ saveCurrentYearMonth );

                counselRef.child(data.getAdviser()).child(current_user_id + counselRandomName).updateChildren(hashMap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(CounselActivity.this, "บันทึกการคำปรึกษาของคุณบันทึกเรียบร้อย.",Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                }else{
                                    Toast.makeText(CounselActivity.this, "เกิดเหตุขัดข้อง.  ",Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left , R.anim.slide_out_right);
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

}
