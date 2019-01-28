package com.example.peetp.myproject;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.enums.Anchor;
import com.example.peetp.myproject.model.Static;
import com.example.peetp.myproject.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaticActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, counselRef, staticRef;
    private String current_user_id, advisor, year, month;
    private Button seachBtn;
    private EditText yearEdt;
    private TextView noExists;
    private Spinner monthSpinner;
    private ProgressBar progressBar;
    private Integer problemPerson, problemStudy, problemOccupation, monthPosition;
    private List<DataEntry> data = new ArrayList<>();
    private Cartesian cartesian;
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        mToolbar = (Toolbar) findViewById(R.id.static_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("ค้นหาสถิติการให้คำปรึกษา");



        seachBtn = (Button) findViewById(R.id.static_seach_btn);
        yearEdt = (EditText) findViewById(R.id.static_year);
        noExists = (TextView) findViewById(R.id.no_exists);
        monthSpinner = (Spinner) findViewById(R.id.static_spinner);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        counselRef = FirebaseDatabase.getInstance().getReference().child("Counsels");
        staticRef = FirebaseDatabase.getInstance().getReference().child("Static");

        final String[] monthString = getResources().getStringArray(R.array.month);
        ArrayAdapter<String> adapterEnglish = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, monthString);
        monthSpinner.setAdapter(adapterEnglish);


        seachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = yearEdt.getText().toString();
                monthPosition = monthSpinner.getSelectedItemPosition();

                if(monthPosition == 0 || monthPosition == null){
                    Toast.makeText(StaticActivity.this, "กรุณาเลือกเดือน", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(year)){
                    Toast.makeText(StaticActivity.this, "กรุณากรอกปี", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(monthPosition < 10){
                        month = "0" +monthPosition;
                    }else {
                        month = monthPosition.toString();
                    }
                    usersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Users users = dataSnapshot.getValue(Users.class);
                                advisor = users.getFullname();
                                staticRef.child(advisor).child(year+month).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Static aStatic = dataSnapshot.getValue(Static.class);
                                            problemPerson = aStatic.getPersonal_problem();
                                            problemOccupation = aStatic.getOccupation_problem();
                                            problemStudy = aStatic.getStudy_problem();
                                            noExists.setVisibility(View.GONE);
                                            Intent clickPostIntent = new Intent(StaticActivity.this, ResultStaticActivity.class);
                                            clickPostIntent.putExtra("problemPerson",problemPerson);
                                            clickPostIntent.putExtra("problemOccupation",problemOccupation);
                                            clickPostIntent.putExtra("problemStudy",problemStudy);
                                            startActivity(clickPostIntent);
                                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                        }
                                        else {
                                            noExists.setVisibility(View.VISIBLE);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                    } );

                }
            }
        });
    }


}
