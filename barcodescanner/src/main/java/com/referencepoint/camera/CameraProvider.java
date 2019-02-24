package com.referencepoint.camera;

import android.media.Image;
import android.util.Size;

import io.reactivex.Flowable;

public interface CameraProvider {

    Flowable<byte[]> preview();

    Size getPreviewSize();

    int getOrientation();

}
