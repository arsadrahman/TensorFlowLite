package com.arsa.mlexample.MainActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.arsa.mlexample.R;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity implements MainActivityContract.View{
    Bitmap imageBitmap;
    ActivityResultLauncher<Intent> intentImagePickerResultLauncher;
    Button predictBtn;
    ImageView imageView;
    MainActivityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        predictBtn = (Button) findViewById(R.id.predictBtn);
        imageView = (ImageView) findViewById(R.id.imageView);
        presenter = new MainActivityPresenter(this,this);
        setOnResultListener();
    }
    public void onClickSelectImage(View view){
           openImagePicker();
    }

    public void onClickPredictImage(View view){
        presenter.predictImageWithTFLiteML(imageBitmap);
    }


    /** intentImagePickerResultLauncher will get the result from Image Picker Intent **/
    private void setOnResultListener() {

        intentImagePickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            //Set image in imageViewer
                            imageView.setImageURI(imageUri);
                            predictBtn.setVisibility(View.VISIBLE);
                            try {
                                imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void updateNameResultAlert(String name) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Hey we found it !")
                .setContentText("It's "+name)
                .setConfirmText("Wanna , try again ?")
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        openImagePicker();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
        }



    private void openImagePicker() {
        Intent intentImagePicker = new Intent(Intent.ACTION_GET_CONTENT);
        intentImagePicker.setType("image/*");
        intentImagePickerResultLauncher.launch(intentImagePicker);
    }
}