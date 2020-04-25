package com.example.rahul.fingerprintpredictor;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;


import junit.framework.Assert;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Helper functions for the TensorFlow image classifier.
 */
public class Helper {

    public static final int IMAGE_SIZE = 1000;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String LABELS_FILE = "imagenet_comp_graph_label_strings.txt";
    public static final String MODEL_FILE = "file:///android_asset/android_model_final.pb";
    public static final String INPUT_NAME = "conv2d_1_input";
    //public static final String OUTPUT_OPERATION = "output";
    public static final String OUTPUT_NAME = "output_node0";
    public static final String[] OUTPUT_NAMES = {OUTPUT_NAME};
    public static final long[] NETWORK_STRUCTURE = {1, IMAGE_SIZE, IMAGE_SIZE, 3};
    public static final int NUM_CLASSES = 1008;
    public static final int Output_size = 40000;

    private static final int MAX_BEST_RESULTS = 3;
    private static final float RESULT_CONFIDENCE_THRESHOLD = 0.1f;

    public static float[] getPixels(Bitmap bitmap, int[] intValues, float[] floatValues) {
        if (bitmap.getWidth() != IMAGE_SIZE || bitmap.getHeight() != IMAGE_SIZE) {
            // rescale the bitmap if needed
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, IMAGE_SIZE, IMAGE_SIZE);
        }

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3] = (((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            floatValues[i * 3 + 2] = ((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
        }
        return floatValues;
    }

    /**
     * Saves a Bitmap object to disk for analysis.
     *
     * @param bitmap The bitmap to save.
     */
}