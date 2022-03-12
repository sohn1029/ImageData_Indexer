package com.sohn.data_maker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void go_info(View view){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
    public void go_start(View view){
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

}