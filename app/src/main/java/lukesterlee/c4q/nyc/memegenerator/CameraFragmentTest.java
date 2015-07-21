package lukesterlee.c4q.nyc.memegenerator;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

/**
 * Created by Luke on 7/21/2015.
 */
public class CameraFragmentTest extends Fragment implements View.OnClickListener {

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;

    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private CameraCharacteristics mCharacteristics;
    private StreamConfigurationMap mStreamConfigsMap;
    private String mCameraId;
    private CameraCaptureSession.CaptureCallback mCaptureCallback;
    private AutoFitTextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private ImageReader mImageReader;
    private CaptureRequest.Builder mPreviewBuilder;
    private Size mPreviewSize;
    private File mFile;
    private int mOrientation;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            try {
                openCamera(width, height);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {

        }
    };


    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;

        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_camera, container);

        mTextureView = (AutoFitTextureView) result.findViewById(R.id.textureView_preview);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);


        return result;
    }

    private void openCamera(int width, int height) throws CameraAccessException {
        setUpCameraOutputs(width, height);
        mCameraManager.openCamera(mCameraId, mStateCallback, null);
    }

    private void setUpCameraOutputs(int width, int height) throws CameraAccessException {
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        mCameraId = mCameraManager.getCameraIdList()[0];
        mCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
        mStreamConfigsMap = mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        mPreviewSize = mStreamConfigsMap.getOutputSizes(SurfaceTexture.class)[0];
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 2);
        //mImageReader.setOnImageAvailableListener();
        mOrientation = getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        } else {
            mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
        }

    }

    private void createPreviewSession() throws CameraAccessException {
        mSurfaceTexture = mTextureView.getSurfaceTexture();
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(mSurfaceTexture);
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        mPreviewBuilder.addTarget(surface);
    }

    private void switchCamera() {

    }



    private void takePicture() {

    }

    private void launchGallery() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_switch:
                switchCamera();
                break;
            case R.id.button_gallery:
                launchGallery();
                break;
            case R.id.button_flash:
                break;
            case R.id.button_take_picture:
                takePicture();
                break;
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}
