package com.mohaalak.example;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mohaalak.fancyalert.FancyAlert;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void success(View v) {
        new FancyAlert.FancyAlertBuilder()
                .setMode(FancyAlert.SUCCESS)
                .setMessage("Success")
                .setDuration(3000)
                .setOnDismissListener(onDismissListener)
                .build(this)
                .show();
    }

    public void error(View v) {
        new FancyAlert.FancyAlertBuilder()
                .setMessage("Error")
                .setDuration(3000)
                .setButtonText("salam")
                .build(this)
                .show();
    }

    public void warning(View v) {
        new FancyAlert.FancyAlertBuilder()
                .setMode(FancyAlert.WARNING)
                .setBackgroundColor(Color.parseColor("#9b59b6"))
                .setMessage("Warning")
                .setDuration(3000)
                .setOnDismissListener(onDismissListener)
                .build(this)
                .show();
    }

    public void withButton(View v) {

    }

    FancyAlert.OnDismissListener onDismissListener = new FancyAlert.OnDismissListener() {
        @Override
        public void dismiss() {
            Toast.makeText(getApplicationContext(), "Dismissed", Toast.LENGTH_SHORT).show();
        }
    };
}
