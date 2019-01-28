package com.example.peetp.myproject;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;


import java.util.ArrayList;
import java.util.List;

public class ResultStaticActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressBar progressBar;
    private List<DataEntry> data = new ArrayList<>();
    private Cartesian cartesian;
    private AnyChartView anyChartView;
    private Integer problemPerson, problemOccupation, problemStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_static);

        mToolbar = (Toolbar) findViewById(R.id.result_static_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("ผลการค้นหา");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
        anyChartView.setProgressBar(progressBar);

        Intent mIntent = getIntent();
        problemPerson = mIntent.getIntExtra("problemPerson", 0);
        problemOccupation = mIntent.getIntExtra("problemOccupation", 0);
        problemStudy = mIntent.getIntExtra("problemStudy", 0);




        cartesian = AnyChart.column();

        data.add(new ValueDataEntry("ปัญหาส่วนตัว", problemPerson));
        data.add(new ValueDataEntry("ปัญหาการเรียน", problemStudy));
        data.add(new ValueDataEntry("ปัญหาการงานอาชีพ", problemOccupation));

        Column column = cartesian.column(data);
        column.fill("#df4c33", 1);
        column.stroke("#FFFFFF");
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("สถิติการให้การปรึกษารายเดือน");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        cartesian.xAxis(0).labels().fontSize(8);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("ประเภทการให้คำปรึกษา");
        cartesian.yAxis(0).title("จำนวน");

        anyChartView.setChart(cartesian);
        ;
    }


}
