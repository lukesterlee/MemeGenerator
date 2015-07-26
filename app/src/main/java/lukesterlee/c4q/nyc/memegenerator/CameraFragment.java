package lukesterlee.c4q.nyc.memegenerator;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private SurfaceHolder mHolder;

    @Bind(R.id.surfaceView_preview) SurfaceView mSurfaceView;
    @Bind(R.id.button_switch) ImageButton mButtonSwitch;
    @Bind(R.id.button_flash) ImageButton mButtonFlash;
    @Bind(R.id.button_setting) Button mButtonSetting;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.button_gallery) ImageView mButtonGallery;


    private Camera mCamera;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Camera.CameraInfo mCameraInfo;
    private int mCameraCount;
    private Camera.Parameters mParameter;

    private boolean isPreview = false;

    private static final String FILENAME_DATE_FORMAT = "yyyy-MM-dd_HH.mm.ss";
    private static final String FILENAME_SUFFIX = ".jpg";
    private static final String DIRECTORY_MEME = "meme_generator";

    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_IMAGE_GET = 2;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null);
            Uri uri = Uri.parse(path);

//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
//            data = stream.toByteArray();
            mProgressBar.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(getActivity(), EditorActivity.class);
            intent.putExtra(Constant.EXTRA_PICTURE_URI_PARCELABLE, uri);
            startActivity(intent);

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_camera, container, false);

        ButterKnife.bind(this, result);

        if(mCamera.getNumberOfCameras() == 1){
            mButtonSwitch.setVisibility(View.INVISIBLE);
        }

        mHolder = mSurfaceView.getHolder();

        mHolder.addCallback(new SurfaceHolder.Callback() {
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

                mCameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(currentCameraId, mCameraInfo);
                mParameter = mCamera.getParameters();

                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera.setDisplayOrientation(270);
                    mParameter.setRotation(90);

                } else {
                    mParameter.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    mCamera.setDisplayOrientation(90);
                    mParameter.setRotation(90);
                }

                Camera.Size s = mParameter.getSupportedPreviewSizes().get(1);
                mParameter.setPreviewSize(s.width, s.height);

                mCamera.setParameters(mParameter);
                mCamera.enableShutterSound(true);

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
                    // mCamera.release()?
                    isPreview = false;
                }
            }
        });
        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GalleryTask().execute();


        getActivity().getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                onChange(selfChange, null);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                Log.i("ciao", "notifyChange: " + uri);
                super.onChange(selfChange, uri);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null==mCamera)
        mCamera = Camera.open(currentCameraId);
        else
            mCamera.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;

        }
    }

    private void selectPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE_GET);
        }
    }

    @OnClick(R.id.button_flash)
    public void switchFlash() {
        if (isPreview) {
            mCamera.stopPreview();
        }
        mCamera.release();

        mParameter = mCamera.getParameters();
        String mode = mParameter.getFlashMode();
        if (mode == null) {
            Toast.makeText(getActivity(), "Front camera doesn't have flash", Toast.LENGTH_SHORT).show();
        } else if (mode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
            mButtonFlash.setBackgroundResource(R.drawable.ic_flash_on_white_48dp);
            mParameter.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(mParameter);
        } else if (mode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            mButtonFlash.setBackgroundResource(R.drawable.ic_flash_auto_white_48dp);
            mParameter.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        } else if (mode.equals(Camera.Parameters.FLASH_MODE_ON)) {
            mButtonFlash.setBackgroundResource(R.drawable.ic_flash_off_white_48dp);
            mParameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        mCamera.setParameters(mParameter);
        mCamera.startPreview();
        isPreview = true;
    }

    @OnClick(R.id.button_switch)
    public void switchCamera() {
        if (isPreview) {
            mCamera.stopPreview();
        }
        mCamera.release();
        //swap the id of the camera to be used
        switch (currentCameraId) {
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                mButtonFlash.setVisibility(View.INVISIBLE);
                mButtonSwitch.setBackgroundResource(R.drawable.ic_camera_front_white_48dp);
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                break;
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
                mButtonFlash.setVisibility(View.VISIBLE);
                mButtonSwitch.setBackgroundResource(R.drawable.ic_camera_rear_white_48dp);
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                break;
        }
        mCamera = Camera.open(currentCameraId);

        mParameter = mCamera.getParameters();

        try {
            //this step is critical or preview on new camera will no know where to render to
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera.setDisplayOrientation(270);
                mParameter.setRotation(90);
            } else {
                mCamera.setDisplayOrientation(90);
                mParameter.setRotation(90);
            }
            mCamera.setParameters(mParameter);
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        isPreview = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.button_take_picture)
    public void takePicture() {
        if (mCamera != null) {
            mCamera.takePicture(mShutterCallback, null, mJpegCallback);
        }
    }

    private class GalleryTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected Long doInBackground(Void... voids) {
            String[] projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.DATE_TAKEN
            };
            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC LIMIT 1");
            cursor.moveToFirst();
            return cursor.getLong(0);

        }

        @Override
        protected void onPostExecute(Long path) {
            Log.i("he", "id:" + path);
            final Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, path.toString());

            Log.v("he", "uri:" + uri);
            Log.v("he", "size:" + mButtonGallery.getWidth() + ", " + mButtonGallery.getMeasuredWidth());

            if(mButtonGallery.getWidth() <= 0) {
                mButtonGallery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mButtonGallery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Log.v("he", "new size:" + mButtonGallery.getWidth() + ", " + mButtonGallery.getMeasuredWidth());

                        Picasso.with(getActivity())
                                .load(uri)
                                .resize(mButtonGallery.getWidth(), mButtonGallery.getHeight())
                                .centerCrop()
                                .into(mButtonGallery);
                    }
                });
            } else {
                Picasso.with(getActivity())
                        .load(uri)
                        .resizeDimen(R.dimen.button_camera, R.dimen.button_camera)
                        .centerCrop()
                        .into(mButtonGallery);
            }


        }
    }

//    public Size getBestSupportedSize(List<Size> sizes, int width, int height) {
//
//
//    }
}