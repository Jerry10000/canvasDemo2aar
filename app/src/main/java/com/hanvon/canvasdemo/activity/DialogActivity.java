package com.hanvon.canvasdemo.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import com.hanvon.canvasdemo.R;


public class DialogActivity extends Activity {

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        progressBar = findViewById(R.id.pb);
    }
}
