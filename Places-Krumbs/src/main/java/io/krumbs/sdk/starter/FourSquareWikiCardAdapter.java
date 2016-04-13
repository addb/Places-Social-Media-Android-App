package io.krumbs.sdk.starter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by ADDB Inc on 15-03-2016.
 */
public class FourSquareWikiCardAdapter extends RecyclerView.Adapter<FourSquareWikiCardAdapter.ViewHolder> {
    private ArrayList<FourSquareSearchActivity.FourSquareWikiSearch> data;
    private int resource;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FourSquareWikiCardAdapter(ArrayList<FourSquareSearchActivity.FourSquareWikiSearch>data) {
        this.data = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FourSquareWikiCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_wiki_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView name= (TextView) holder.view.findViewById(R.id.wname);
        TextView url= (TextView) holder.view.findViewById(R.id.wurl);
        TextView summary= (TextView) holder.view.findViewById(R.id.wsummary);
        ImageView img= (ImageView) holder.view.findViewById(R.id.wimage);
        final ProgressBar pbar=(ProgressBar)holder.view.findViewById(R.id.pwikiBar);
        //setting all the views
        name.setText(data.get(position).getName());
        url.setText(data.get(position).getUrl());
        summary.setText(data.get(position).getSummary());
        ImageLoader.getInstance().displayImage(data.get(position).getImage(), img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                pbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                pbar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                pbar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                pbar.setVisibility(View.GONE);
            }
        });
        //rating.setRating((Float.parseFloat(data.get(position).getRating())/2));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

}
