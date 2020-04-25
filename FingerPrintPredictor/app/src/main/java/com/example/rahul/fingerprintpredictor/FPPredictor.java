package com.example.rahul.fingerprintpredictor;


import android.content.Context;
import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class FPPredictor {

    private static TensorFlowInferenceInterface inferenceInterface;

    public FPPredictor(String modelPath, Context context) {
        // Loading model from assets folder.
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), modelPath);
    }
    /*
    public Bitmap ModelOutput(Bitmap Input) {
        // Node Names
        String inputName = "input_tensor";
        String outputName = "output_tensor";

        // Define output nodes
        String[] outputNodes = new String[]{outputName};
        float[] outputs = new float[40000];

        //Converting input Of Mat datatype to float[][][]
       // x_2 = placeholder("float", [None, 1, 784], name="input")
        // Feed image into the model and fetch the results.
        //inferenceInterface.feed();
        inferenceInterface.run(outputNodes, false);
        inferenceInterface.fetch(outputName, outputs);

        // Convert one-hot encoded result to an int (= detected class)
        float max = Float.MIN_VALUE;
        int idx = -1;
        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] > max) {
                max = outputs[i];
                idx = i;
            }
        }

        //return idx;
    }
    */
}
