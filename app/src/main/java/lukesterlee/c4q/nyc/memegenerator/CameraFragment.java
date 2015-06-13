package lukesterlee.c4q.nyc.memegenerator;

import android.app.Fragment;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

/**
 * Created by Luke on 6/11/2015.
 */
public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder holder;
    private FloatingActionButton mButtonTakePicture;

    private Button mButtonGallery;
    private Button mButtonSwitch;
    private Button mButtonFlash;
    private Button mButtonSetting;

    private ProgressBar mProgressBar;

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private boolean isPreview = false;

    static final int REQUEST_CODE_TAKE_PHOTO = 1;
    static final int REQUEST_CODE_IMAGE_GET = 2;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String filename;

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_camera, container, false);

        mProgressBar = (ProgressBar) result.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mButtonTakePicture = (FloatingActionButton) result.findViewById(R.id.button_floating);
        mButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });

        mButtonSwitch = (Button) result.findViewById(R.id.button_switch);
        if(mCamera.getNumberOfCameras() == 1){
            mButtonSwitch.setVisibility(View.INVISIBLE);
        }




        mButtonGallery = (Button) result.findViewById(R.id.button_gallery);
        mButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto(v);
            }
        });

        mButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "hello!", Toast.LENGTH_SHORT);
            }
        });

        mSurfaceView = (SurfaceView) result.findViewById(R.id.surfaceView_camera);
        holder = mSurfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null)
                    return;

                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                    isPreview = true;
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    isPreview = false;
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    isPreview = false;
                }
            }
        });


        mButtonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPreview) {
                    mCamera.stopPreview();
                }

                mCamera.release();

                //swap the id of the camera to be used
                switch (currentCameraId) {
                    case Camera.CameraInfo.CAMERA_FACING_BACK:
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        break;
                    case Camera.CameraInfo.CAMERA_FACING_FRONT:
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                        break;
                }

                mCamera = Camera.open(currentCameraId);

                try {
                    //this step is critical or preview on new camera will no know where to render to
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            }
        });


        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open(currentCameraId);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;

        for (Camera.Size size : sizes) {
            int area = size.width * size.height;
            if (area > largestArea) {
                bestSize = size;
                largestArea = area;
            }
        }
        return bestSize;
    }

    private void selectPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE_GET);
        }

    }
}
