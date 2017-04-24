package com.bignerdranch.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "PhotoGalleryFragment";
    private static final int COLUMN_WIDTH_IN_DP = 250;

    private RecyclerView mPhotoRecyclerView;
    private PhotoAdapter mPhotoAdapter = new PhotoAdapter(new ArrayList());
    private List<GalleryItem> mItems = new ArrayList<>();
    private int currentPage = 1;
    private int currentScrollPosition = 0;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute(new Integer(currentPage));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mPhotoRecyclerView.addOnScrollListener(new PhotosScrollListener());
        mPhotoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        if(isAdded()) {
            if(mPhotoRecyclerView.getAdapter() == null) {
                mPhotoRecyclerView.setAdapter(mPhotoAdapter);
            } else {

                mPhotoAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onGlobalLayout() {
        if(mPhotoRecyclerView != null) {
            int columnCount = mPhotoRecyclerView.getWidth() / COLUMN_WIDTH_IN_DP;
            ((GridLayoutManager)mPhotoRecyclerView.getLayoutManager()).setSpanCount(columnCount);
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        List<GalleryItem> mGalleryItems;

        public PhotoAdapter (List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            photoHolder.bindGalleryItem(mGalleryItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class PhotosScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            if(dy > 0) //check for scroll down
            {
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                View lastChildinView = recyclerView.getChildAt(visibleItemCount - 1);
                int lastChildInViewPosition = recyclerView.getLayoutManager().getPosition(lastChildinView);

                if(lastChildInViewPosition == (totalItemCount - 1)) {
                    currentPage++;
                    View firstChildinView = recyclerView.getChildAt(0);
                    int firstChildInViewPosition = recyclerView.getLayoutManager().getPosition(firstChildinView);
                    currentScrollPosition = firstChildInViewPosition;
                    new FetchItemsTask().execute(new Integer(currentPage));
                }
            }
        }
    }

    public class FetchItemsTask extends AsyncTask<Integer, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {
            return new FlickrFetcher().fetchItems(params[0]);
        }

        @Override
        public void onPostExecute(List<GalleryItem> items) {
            mItems.addAll(items);
            mPhotoAdapter.mGalleryItems = mItems;
            setupAdapter();
        }
    }
}
