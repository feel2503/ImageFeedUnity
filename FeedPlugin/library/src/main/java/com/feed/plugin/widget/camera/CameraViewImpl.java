package com.feed.plugin.widget.camera;

import android.graphics.Matrix;
import android.view.TextureView;

import java.util.Set;

abstract class CameraViewImpl {

    protected final Callback mCallback;

    public CameraViewImpl(Callback callback) {
        mCallback = callback;
    }

    abstract TextureView.SurfaceTextureListener getSurfaceTextureListener();

    abstract void start();

    abstract void stop();

    abstract boolean isCameraOpened();

    abstract void setFacing(int facing);

    abstract int getFacing();

    abstract Set<AspectRatio> getSupportedAspectRatios();

    abstract void setAspectRatio(AspectRatio ratio);

    abstract AspectRatio getAspectRatio();

    abstract void setAutoFocus(boolean autoFocus);

    abstract boolean getAutoFocus();

    abstract void setFlash(int flash);

    abstract int getFlash();

    abstract void takePicture();

    abstract void setDisplayOrientation(int displayOrientation);

    interface Callback {

        void onCameraOpened();

        void onCameraClosed();

        void onPictureTaken(byte[] data);

        void onTransformUpdated(Matrix matrix);

    }

}
