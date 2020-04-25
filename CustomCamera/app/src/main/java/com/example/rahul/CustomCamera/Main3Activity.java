package com.example.rahul.CustomCamera;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Canvas;
//import android.graphics.Paint;
public class Main3Activity extends AppCompatActivity {
    String FingerDir;
    Canvas canvas;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FingerDir = extras.getString("DirName");
        }
        //Button photoButton = (Button) findViewById(R.id.capture);

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    //ImageView mImageView = null;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    public void OnButtonClick(View v)
    {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Toast.makeText(getApplicationContext(),"asdfasdfasdf'", Toast.LENGTH_SHORT).show();
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.rahul.CustomCamera.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                //  Find Screen size first  
                DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                int screenWidth = metrics.widthPixels;
                int screenHeight = (int) (metrics.heightPixels*0.9);

                //  Set paint options  
                paint.setAntiAlias(true);
                paint.setStrokeWidth(3);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.argb(255, 255, 255, 255));

                //canvas.drawLine((screenWidth/3)*2,0,(screenWidth/3)*2,screenHeight,paint);
                //canvas.drawLine((screenWidth/3),0,(screenWidth/3),screenHeight,paint);
                //canvas.drawLine(0,(screenHeight/3)*2,screenWidth,(screenHeight/3)*2,paint);
                //canvas.drawLine(0,(screenHeight/3),screenWidth,(screenHeight/3),paint);
                final Rect r = new Rect((screenWidth/2-1000),(screenHeight/2-1000),(screenWidth/2+1000),(screenHeight/2+1000));
                canvas.drawRect(r,paint);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(FingerDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
