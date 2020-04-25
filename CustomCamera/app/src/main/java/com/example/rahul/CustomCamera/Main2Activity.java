package com.example.rahul.CustomCamera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.File;

public class Main2Activity extends AppCompatActivity {

    String Dir;
    File FingerDir, gn, sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            Dir = extras.getString("DirName");
        }
    }



    public void OnAnyButtonClick(View v)
    {

        FingerDir = new File(Dir+"/"+v.getTag());
        gn = new File(FingerDir+"/"+"gn");
        sp = new File(FingerDir+"/"+"sp");

        if(!FingerDir.exists())
        {
            FingerDir.mkdir();
        }
        if(!gn.exists())
        {
            gn.mkdir();
        }
        if(!sp.exists())
        {
            sp.mkdir();
        }

        //Toast.makeText(getApplicationContext(),FingerDir.toString(), Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(Main2Activity.this, Main3Activity.class);
        intent1.putExtra("DirName",gn.getAbsolutePath());
        startActivity(intent1);
    }


}
