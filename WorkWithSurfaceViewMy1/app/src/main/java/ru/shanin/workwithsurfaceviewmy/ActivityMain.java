package ru.shanin.workwithsurfaceviewmy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.shanin.workwithsurfaceviewmy.surfaceview.ViewDraw;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ViewDraw(this));
    }
}