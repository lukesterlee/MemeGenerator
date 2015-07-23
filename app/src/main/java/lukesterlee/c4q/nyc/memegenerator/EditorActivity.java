package lukesterlee.c4q.nyc.memegenerator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditorActivity extends Activity {

    private Uri mUri;
    private Bitmap mBitmap;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mImageView = (ImageView) findViewById(R.id.imageView_editor);

        mUri = getIntent().getParcelableExtra(Constant.EXTRA_PICTURE_URI_PARCELABLE);
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
            Picasso.with(this).load(mUri).into(mImageView);
            //getContentResolver().delete(mUri, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}



//String date = new SimpleDateFormat(FILENAME_DATE_FORMAT).format(new Date());
//String filename = date + FILENAME_SUFFIX;
//FileOutputStream os = null;
//boolean success = true;
//
//try {
//        os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
//        os.write(data);
//        } catch (Exception e) {
//        Log.e(TAG, "Error writing to file " + filename, e);
//        success = false;
//        } finally {
//        try {
//        if (os != null)
//        os.close();
//        } catch (Exception e) {
//        Log.e(TAG, "Error closing file " + filename, e);
//        success = false;
//        }
//        }
//        if (success) {
//        Toast.makeText(getActivity().getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
//        }
//        getActivity().finish();
