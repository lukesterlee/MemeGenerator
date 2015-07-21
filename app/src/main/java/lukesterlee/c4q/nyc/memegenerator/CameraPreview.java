package lukesterlee.c4q.nyc.memegenerator;

import android.content.Context;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private CameraDevice mCamera;
    private CameraManager mManager;

    public CameraPreview(Context context, CameraDevice mCamera) {
        super(context);
        this.mCamera = mCamera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mManager.
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mManager.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
