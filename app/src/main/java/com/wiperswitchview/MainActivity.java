package com.wiperswitchview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wiperswitchview.view.WiperSwitch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WiperSwitch wiperSwitch = (WiperSwitch) findViewById(R.id.switchbtn);
        wiperSwitch.setOnCheckChangeListener(new WiperSwitch.OnCheckChangeListener() {
            @Override
            public void onCheckChanged(View v, boolean isOpen) {
                if (isOpen){
                    Toast.makeText(MainActivity.this,"开",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this,"关",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
