package com.arsa.mlexample.MainActivity;

import android.graphics.Bitmap;

interface MainActivityContract {

    interface View{
        void updateNameResultAlert(String name);
    }
    interface Presenter{
        void predictImageWithTFLiteML(Bitmap bitmap);
    }
}
