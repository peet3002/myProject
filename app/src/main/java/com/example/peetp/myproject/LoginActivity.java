package com.example.peetp.myproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peetp.myproject.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText useremail, userPassword;
    private TextView needNewAccountLink, text1, text2;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        needNewAccountLink = (TextView) findViewById(R.id.register_account_link);
        text1 = (TextView) findViewById(R.id.login_text1);
        text2 = (TextView) findViewById(R.id.login_text2);
        useremail = (EditText) findViewById(R.id.login_email);
        userPassword = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);

        loadingBar = new ProgressDialog(this);

        Typeface extraLight = Typeface.createFromAsset(getAssets(), "fonts/Prompt-ExtraLight.ttf");
        needNewAccountLink.setTypeface(extraLight);
        text1.setTypeface(extraLight);
        text2.setTypeface(extraLight);
        useremail.setTypeface(extraLight);
        userPassword.setTypeface(extraLight);
        loginButton.setTypeface(extraLight);

        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowingUserToLogin();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserToMainActivity();
        }
    }

   
    private void AllowingUserToLogin() {
        String email = useremail.getText().toString();
        String password = userPassword.getText().toString();


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "กรุณาใส่อีเมล์...", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "กรุณาใส่รหัสผ่าน...", Toast.LENGTH_SHORT).show();
            }
            else{
                loadingBar.setTitle("กำลังเข้าสู่ระบบ");
                loadingBar.setMessage("กรุณารอสักครู่กำลังเข้าสู่ระบบชองคุณ...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    SendUserToMainActivity();
                                    Toast.makeText(LoginActivity.this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }else{
                                    String message = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "เกิดเหตุขัดข้อง: " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
            }
        }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}

