package com.feed.plugin.widget.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.feed.plugin.ImgSelectActivity;
import com.feed.plugin.widget.cropimgview.util.CropUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;


public class CameraPreview extends Thread {
    private final static String TAG = "CameraPreview : ";

    private Size mPreviewSize;
    private Context mContext;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private TextureView mTextureView;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private CameraManager mCameraManager;
    private String mCameraID = "0";

    private String imgName = "/camera_pic.jpg";

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    // focus
    private boolean mManualFocusEngaged;
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public CameraPreview(Context context, TextureView textureView) {
        mContext = context;
        mTextureView = textureView;
//        mTextureView.setOnTouchListener(mOnTouchListener);

        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    private String getBackFacingCameraId(CameraManager cManager) {
        try {
            for (final String cameraId : cManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_BACK)
                    return cameraId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getmCameraID(){
        return mCameraID;
    }

    public void setmCameraID(String mCameraID){
        this.mCameraID = mCameraID;
    }

    public void openCamera()
    {
        openCamera(mCameraID);
    }

    public void openCamera(String cameraID)
    {
        //CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "openCamera E");
        try {
            //String cameraId = getBackFacingCameraId(mCameraManager);
            //mCameraID = getBackFacingCameraId(mCameraManager);
            mCameraID = cameraID;

            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraID);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            mCameraManager.openCamera(mCameraID, mStateCallback, null);

//            int permissionCamera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
//            if(permissionCamera == PackageManager.PERMISSION_DENIED) {
//                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, MainActivity.REQUEST_CAMERA);
//            } else {
//                manager.openCamera(cameraId, mStateCallback, null);
//            }



        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        startBackgroundThread();

        Log.e(TAG, "openCamera X");
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onSurfaceTextureAvailable, width="+width+",height="+height);
            String cameraId = getBackFacingCameraId(mCameraManager);
            openCamera(cameraId);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onSurfaceTextureUpdated");
        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onOpened");
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onError");
        }

    };

    protected void startPreview() {
        // TODO Auto-generated method stub
        if(null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            Log.e(TAG, "startPreview fail, return");
        }

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if(null == texture) {
            Log.e(TAG,"texture is null, return");
            return;
        }

        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);

        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    // TODO Auto-generated method stub
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    // TODO Auto-generated method stub
                    Toast.makeText(mContext, "onConfigureFailed", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        // TODO Auto-generated method stub
        if(null == mCameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }

        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

//        HandlerThread thread = new HandlerThread("CameraPreview");
//        thread.start();
//        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setSurfaceTextureListener()
    {
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        startBackgroundThread();
        setSurfaceTextureListener();
    }

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    public void onPause() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onPause");
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
                Log.d(TAG, "CameraDevice Close");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
        stopBackgroundThread();
    }

//    public void switchFlash() {
//        try {
//            if (cameraId.equals("0")) {
//                if (isFlashSupported) {
//                    if (isTorchOn) {
//                        mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
//                        mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
//                        flashButton.setImageResource(R.drawable.ic_flash_off);
//                        isTorchOn = false;
//                    } else {
//                        mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
//                        mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
//                        flashButton.setImageResource(R.drawable.ic_flash_on);
//                        isTorchOn = true;
//                    }
//                }
//            }
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setupFlashButton() {
//        if (cameraId.equals(CAMERA_BACK) && isFlashSupported) {
//            flashButton.setVisibility(View.VISIBLE);
//
//            if (isTorchOn) {
//                flashButton.setImageResource(R.drawable.ic_flash_off);
//            } else {
//                flashButton.setImageResource(R.drawable.ic_flash_on);
//            }
//
//        } else {
//            flashButton.setVisibility(View.GONE);
//        }
//    }

    public void flashMode(boolean isFlshOn)
    {
        try{
            mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
            //mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                mCameraManager.setTorchMode(mCameraID, true);
//                //mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
//            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void takePicture(final Activity parentActivity)
    {
        if(mCameraDevice == null) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        //CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            String dirPath = CropUtils.getDirPath();
            final File file = new File(dirPath + imgName);
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(mContext, "Saved:" + file, Toast.LENGTH_SHORT).show();

                    String filePath = CropUtils.getDirPath() + imgName;
                    ArrayList<String> arrImg = new ArrayList<String>();
                    arrImg.add(filePath);

                    ((ImgSelectActivity)parentActivity).startResultActivity(arrImg);

                }
            };

            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

//    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener(){
//        @Override
//        public boolean onTouch(View v, MotionEvent event)
//        {
//            final int actionMasked = event.getActionMasked();
//            if (actionMasked != MotionEvent.ACTION_DOWN) {
//                return false;
//            }
//            if (mManualFocusEngaged) {
//                Log.d(TAG, "Manual focus already engaged");
//                return true;
//            }
//
//            final Rect sensorArraySize = mCameraInfo.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
//
//            //TODO: here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
//            final int y = (int)((event.getX() / (float)v.getWidth())  * (float)sensorArraySize.height());
//            final int x = (int)((event.getY() / (float)v.getHeight()) * (float)sensorArraySize.width());
//            final int halfTouchWidth  = 150; //(int)motionEvent.getTouchMajor(); //TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
//            final int halfTouchHeight = 150; //(int)motionEvent.getTouchMinor();
//            MeteringRectangle focusAreaTouch = new MeteringRectangle(Math.max(x - halfTouchWidth,  0),
//                    Math.max(y - halfTouchHeight, 0),
//                    halfTouchWidth  * 2,
//                    halfTouchHeight * 2,
//                    MeteringRectangle.METERING_WEIGHT_MAX - 1);
//
//            CameraCaptureSession.CaptureCallback captureCallbackHandler = new CameraCaptureSession.CaptureCallback() {
//                @Override
//                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
//                    super.onCaptureCompleted(session, request, result);
//                    mManualFocusEngaged = false;
//
//                    if (request.getTag() == "FOCUS_TAG") {
//                        //the focus trigger is complete -
//                        //resume repeating (preview surface will get frames), clear AF trigger
//                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
//                        mCameraOps.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
//                    }
//                }
//
//                @Override
//                public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
//                    super.onCaptureFailed(session, request, failure);
//                    Log.e(TAG, "Manual AF failure: " + failure);
//                    mManualFocusEngaged = false;
//                }
//            };
//
//            //first stop the existing repeating request
//            mCameraOps.stopRepeating();
//
//            //cancel any existing AF trigger (repeated touches, etc.)
//            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
//            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
//            mCameraOps.capture(mPreviewRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);
//
//            //Now add a new AF trigger with focus region
//            if (isMeteringAreaAFSupported()) {
//                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
//            }
//            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
//            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
//            mPreviewRequestBuilder.setTag("FOCUS_TAG"); //we'll capture this later for resuming the preview
//
//            //then we ask for a single request (not repeating!)
//            mCameraOps.capture(mPreviewRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);
//            mManualFocusEngaged = true;
//
//            return true;
//        }
//    };
//
//    private boolean isMeteringAreaAFSupported() {
//        return mCameraInfo.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
//    }


}