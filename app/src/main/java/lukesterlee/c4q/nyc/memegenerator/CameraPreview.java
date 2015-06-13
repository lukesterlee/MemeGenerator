package lukesterlee.c4q.nyc.memegenerator;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

/**
 * Created by Luke on 6/13/2015.
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;

    public CameraPreview(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

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
