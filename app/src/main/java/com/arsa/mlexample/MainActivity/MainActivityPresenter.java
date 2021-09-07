package com.arsa.mlexample.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;

import com.arsa.mlexample.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class MainActivityPresenter implements MainActivityContract.Presenter{
    MainActivityContract.View view;
    Context context;

    public MainActivityPresenter(Context context, MainActivityContract.View view){
        this.view = view;
        this.context = context;

    }

    @Override
    public void predictImageWithTFLiteML(Bitmap bitmap) {

        //First Scale down the Original image into Model size image Which is 224, 224
        Bitmap rescaledImage = Bitmap.createScaledBitmap(bitmap,224,224,true);
        try {
            MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(context);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
            TensorImage tensorImage = TensorImage.fromBitmap(rescaledImage);
            ByteBuffer byteBuffer = tensorImage.getBuffer();
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //outputFeature0 holds the result of the predicts
            findResultAndUpdateResultView(getLabelIndex(outputFeature0.getFloatArray()));



            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    private void findResultAndUpdateResultView(int labelIndex) {
        String fileName = "labels.txt";
        List<String> labels = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getApplicationContext().getAssets().open(fileName), "UTF-8"));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                labels.add(mLine);
            }
        } catch (IOException e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }
        view.updateNameResultAlert(labels.get(labelIndex));
    }

    private int getLabelIndex(float[] array) {
        int index = 0;
        float minimumValue = 0.0f;
        for(int i = 0;i<1000;i++){
            if(array[i]>minimumValue){
                index = i;
                minimumValue = array[i];
            }
        }

        return index;
    }
}
