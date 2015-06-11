package lukesterlee.c4q.nyc.memegenerator;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {

    static final int REQUEST_CODE_TAKE_PHOTO = 1;
    static final int REQUEST_CODE_IMAGE_GET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CameraFragment())
                    .commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // choose a picture from gallery
        if (requestCode == REQUEST_CODE_IMAGE_GET && resultCode == RESULT_OK) {

            Uri fullPhotoUri = data.getData();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new EditorFragment())
                    .commit();
//            Intent ramona = new Intent(getApplicationContext(), EditorActivity.class);
//            ramona.putExtra("uri", fullPhotoUri);
//            startActivity(ramona);



        }
        // after taking a picture, if the user presses OK button
        else if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            //imageUri = (Uri) getIntent().getExtras().get(MediaStore.EXTRA_OUTPUT);
//            Uri selectedImage = imageUri;
//            getContentResolver().notifyChange(selectedImage, null);
//            addPictureToGallery(selectedImage);
//            Intent ramona = new Intent(getApplicationContext(), EditorActivity.class);
//            ramona.putExtra("uri", selectedImage);
//            startActivity(ramona);

        }

    }
}
