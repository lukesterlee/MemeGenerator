package lukesterlee.c4q.nyc.memegenerator;

import android.app.Fragment;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.view.TextureView;

/**
 * Created by Luke on 7/21/2015.
 */
public class CameraFragmentTest extends Fragment {

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;

    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private String[] mCameraIdList;
    private String mCameraId;
    private CameraDevice.StateCallback mStateCallback;
    private CameraCaptureSession.CaptureCallback mCaptureCallback;
    private TextureView mTextureView;
    private ImageReader mImageReader;
    private CaptureRequest.Builder mPreviewBuilder;
    private Handler mHandler;


    private void switchCamera() {
        
    }

    private void setUpCamera() {

    }

    private void takePicture() {

    }

}
