package com.example.rahul.fingerprintpredictor;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    TensorFlowImageClassifier FPmodel;

    File MainDir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File Root = Environment.getExternalStorageDirectory();
        //makeText(getApplicationContext(),Root.getAbsolutePath(),LENGTH_LONG).show();
        // This Main directory is to create it for the first time only
        MainDir = new File(Root.getAbsolutePath()+"/TestFolder");
        if(!MainDir.exists())
        {
            MainDir.mkdir();
        }

        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0,
                MainActivity.this, mOpenCVCallBack)) {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }

        //Loading the Finger Print Predictor Model
        FPmodel = new TensorFlowImageClassifier(this);
        ImageView imageView = (ImageView) findViewById(R.id.picture);
        ImageView imageView1 = (ImageView) findViewById(R.id.picture1);

    }
    Mat FMat;
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //your code
                    FMat = new Mat(200,200, CvType.CV_32F);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //ImageView mImageView = null;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;
    int[] pixels = new int[10000000];
    //int[] pixels;
    //private float[] floatValues;
    float[] Output_pixels;
    Bitmap resized;
    File FingerPrint;
    Bitmap OutputImage;


    public void OnButtonClick(View v)
    {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Toast.makeText(getApplicationContext(),"asdfasdfasdf'", Toast.LENGTH_SHORT).show();
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.rahul.FingerPrintPredictor.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                //Toast.makeText(getApplicationContext(),photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            final Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            Bitmap OriginalPhoto = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
             Bitmap croppedPhoto = Bitmap.createBitmap(
                     OriginalPhoto,
                     OriginalPhoto.getWidth()/2 - 1000,
                     OriginalPhoto.getHeight()/2 - 1000,
                     2000,
                     2000
            );
            resized = Bitmap.createScaledBitmap(croppedPhoto, 1000, 1000, true);

            File resizedPhoto = null;
            try {
                resizedPhoto = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream fout = new FileOutputStream(resizedPhoto);
                resized.compress(Bitmap.CompressFormat.JPEG, 100, fout); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                //fout.flush(); // Not really required
                fout.close(); // do not forget to close the stream
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(resized.getWidth());
            ImageView imageView = (ImageView) findViewById(R.id.picture);
            imageView.setImageBitmap(resized);
            //resized.getPixels(pixels,0, resized.getWidth(),0, 0, resized.getWidth(), resized.getHeight());
            //System.out.println(pixels[2000000]);

            //Mat m = Imgcodecs.imread(resizedPhoto.getAbsolutePath());

            new Thread((new Runnable() {
                @Override
                public void run() {
                    try {
                        int[] OutInt = new int[40000];
                        Output_pixels = FPmodel.doRecognize(resized);
                        System.out.println("Got the output from the model");
                        for(int i =0; i< Output_pixels.length;i++) {
                            OutInt[i] =Math.round(Output_pixels[i]);
                        }

                        for(int i =0; i<Output_pixels.length;i++)
                        {
                            Output_pixels[i] = Output_pixels[i]*255;
                            //System.out.print(Output_pixels[i]);
                            //System.out.println(",");
                        }

                        BufferedWriter br = new BufferedWriter(new FileWriter(MainDir.getAbsoluteFile()+"myfile.csv"));
                        StringBuilder sb = new StringBuilder();
                        for(int i =0; i<Output_pixels.length;i++)
                        {
                            sb.append(Output_pixels[i]);
                            sb.append(",");
                        }

                        br.write(sb.toString());
                        br.close();

                        System.out.print("Length of Output_Pixels:");
                        System.out.println(Output_pixels.length);
                        FMat.put(0,0,Output_pixels);

                        //Imgproc.cvtColor(FMat, FMat, Imgproc.COLOR_RGB2GRAY);
                        OutputImage = Bitmap.createBitmap(FMat.cols(), FMat.rows(), Bitmap.Config.ARGB_8888);
                        System.out.println("CreateBitmap is done");

                        //Utils.matToBitmap(FMat, OutputImage);}

                        System.out.println(FMat);
                        System.out.println("Inside try :");
                        //Bitmap OutputImage = Bitmap.createBitmap(OutInt,200,200,Bitmap.Config.ALPHA_8);


                        try {
                            FingerPrint = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Imgcodecs.imwrite(FingerPrint.getAbsolutePath(),FMat);

                        System.out.println("Saved the output");

                        runOnUiThread(new Runnable() {
                            public void run() {
                                //To test the image
                                Bitmap trial = BitmapFactory.decodeFile(FingerPrint.getAbsolutePath());
                                ImageView imageView1 = (ImageView) findViewById(R.id.picture1);
                                imageView1.setImageBitmap(trial);
                                Toast.makeText(MainActivity.this, "FingerPrint displayed", Toast.LENGTH_SHORT).show();
                            }
                        });

                        /*
                        try {
                            FingerPrint = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            FileOutputStream fout = new FileOutputStream(FingerPrint);
                            OutputImage.compress(Bitmap.CompressFormat.JPEG, 100, fout); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                            //fout.flush(); // Not really required
                            fout.close(); // do not forget to close the stream
                            System.out.println("Stored the Bitmap");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/

                    }
                    catch (Exception e)
                    {
                        System.out.println("Inside Catch:");
                        e.printStackTrace();
                    }
                }
            })).start();



        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = new File(MainDir.toString());
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
