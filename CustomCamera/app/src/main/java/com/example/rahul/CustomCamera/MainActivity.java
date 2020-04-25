package com.example.rahul.CustomCamera;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    EditText dirName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dirName = findViewById(R.id.editText);
    }

    public void CreateFolderwithName(View v)
    {
        String PersonName = dirName.getText().toString();
        //makeText(getApplicationContext(), PersonName, LENGTH_LONG).show();
       if(PersonName.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Specify your Name",Toast.LENGTH_SHORT).show();
        }
       else {

            File Root = Environment.getExternalStorageDirectory();
            //makeText(getApplicationContext(),Root.getAbsolutePath(),LENGTH_LONG).show();
            // This Main directory is to create it for the first time only
            File MainDir = new File(Root.getAbsolutePath()+"/FingerImages");
            if(!MainDir.exists())
            {
                MainDir.mkdir();
            }
            File Dir = new File(MainDir+"/"+PersonName);
            //makeText(getApplicationContext(),Dir.getAbsolutePath(),LENGTH_SHORT).show();
            if(!Dir.exists())
            {
                if(Dir.mkdir())
                    Toast.makeText(getApplicationContext(), String.format("Directory created%s", Dir), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Some error", LENGTH_SHORT).show();
            }

           Intent intent = new Intent(MainActivity.this, Main2Activity.class);
           intent.putExtra("DirName",Dir.getAbsolutePath());
           startActivity(intent);

        }

    }
}
