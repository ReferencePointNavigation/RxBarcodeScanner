package com.referencepoint.camera;

import android.media.Image;
import android.util.Size;

import io.reactivex.Flowable;

public interface CameraProvider {

    Size getPreviewSize();

    int getOrientation();

    Flowable<byte[]> preview();

    void close();

}
