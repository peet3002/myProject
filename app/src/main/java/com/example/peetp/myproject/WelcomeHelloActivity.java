package com.example.peetp.myproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeHelloActivity extends AppCompatActivity {

    TextView textView;
    Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_hello);

        textView = (TextView) findViewById(R.id.text);
        loginBtn = (Button) findViewById(R.id.hello_login);
        registerBtn = (Button) findViewById(R.id.hello_register);

        Typeface extraLight = Typeface.createFromAsset(getAssets(), "fonts/Prompt-ExtraLight.ttf");

        textView.setTypeface(extraLight);
        loginBtn.setTypeface(extraLight);
        registerBtn.setTypeface(extraLight);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(WelcomeHelloActivity.this, LoginActivity.class);
                startActivity(mainIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(WelcomeHelloActivity.this, RegisterActivity.class);
                startActivity(mainIntent);
            }
        });

    }
}
