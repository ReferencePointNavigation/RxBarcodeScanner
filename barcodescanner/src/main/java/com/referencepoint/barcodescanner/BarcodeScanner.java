package com.referencepoint.barcodescanner;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.util.Size;

import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.referencepoint.camera.CameraProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class BarcodeScanner {

    private DecoderFactory mDecoderFactory;

    private Decoder mDecoder;

    private CameraProvider mCameraProvider;

    public BarcodeScanner(CameraProvider cameraProvider) {
        mCameraProvider = cameraProvider;
        mDecoderFactory = new DefaultDecoderFactory();
        mDecoder = createDecoder();
    }

    public Flowable<BarcodeResult> scan() {
        return mCameraProvider
            .preview()
            .flatMap(this::decodeImage)
            .subscribeOn(Schedulers.computation());
    }

    public void pause() {

    }

    private Decoder createDecoder() {
        DecoderResultPointCallback callback = new DecoderResultPointCallback();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, callback);
        Decoder decoder = mDecoderFactory.createDecoder(hints);
        callback.setDecoder(decoder);
        return decoder;
    }

    private Flowable<BarcodeResult> decodeImage(byte[] data) {
        return Flowable.create(emitter -> {
            Size size = mCameraProvider.getPreviewSize();
            int orientation = mCameraProvider.getOrientation();
            SourceData sourceData = new SourceData(data, size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, orientation);
            sourceData.setCropRect(new Rect(0, 0, size.getHeight(), size.getWidth()));
            BarcodeResult result = decode(sourceData);
            if (result != null) {
                emitter.onNext(result);
            }
        }, BackpressureStrategy.BUFFER);
    }

    private BarcodeResult decode(SourceData sourceData) {
        Result rawResult = null;

        LuminanceSource source = sourceData.createSource();

        if(source != null) {
            rawResult = mDecoder.decode(source);
        }
        if (rawResult != null) {
            return new BarcodeResult(rawResult, sourceData);
            //Message message = Message.obtain(resultHandler, R.id.zxing_decode_succeeded, barcodeResult);
        } else {
            //Message message = Message.obtain(resultHandler, R.id.zxing_decode_failed);
        }
        List<ResultPoint> resultPoints = mDecoder.getPossibleResultPoints();
        //Message message = Message.obtain(resultHandler, R.id.zxing_possible_result_points, resultPoints);
        return null;
    }

}
