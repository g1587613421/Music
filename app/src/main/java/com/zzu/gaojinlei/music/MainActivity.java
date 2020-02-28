package com.zzu.gaojinlei.music;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.qmuiteam.qmui.link.QMUIScrollingMovementMethod;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView)findViewById(R.id.lv)).setMovementMethod(new QMUIScrollingMovementMethod());
    }
}
