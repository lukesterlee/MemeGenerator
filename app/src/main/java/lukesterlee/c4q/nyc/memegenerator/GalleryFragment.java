package lukesterlee.c4q.nyc.memegenerator;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

public class GalleryFragment extends Fragment {


    private GridView mGridView;
    private GalleryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_gallery, null);

        mGridView = (GridView) result.findViewById(R.id.gridView_gallery);

        return result;
    }

    private class GalleryTask extends AsyncTask<Void, Void, List<Uri>> {
        @Override
        protected List<Uri> doInBackground(Void... params) {


            return null;
        }

        @Override
        protected void onPostExecute(List<Uri> uris) {
            mAdapter = new GalleryAdapter(getActivity(), uris);
            mGridView.setAdapter(mAdapter);
        }
    }
}
