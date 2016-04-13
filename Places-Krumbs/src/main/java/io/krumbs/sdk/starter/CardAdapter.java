package io.krumbs.sdk.starter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private ArrayList<CardContent> data;
    private int resource;
    Context context;
    Resources res;
    String img;


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
    public CardAdapter(ArrayList<CardContent>data) {
        this.data = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView caption= (TextView) holder.view.findViewById(R.id.feedCaption);
        ImageView image= (ImageView) holder.view.findViewById(R.id.feedImage);
        ImageView emo=(ImageView)holder.view.findViewById(R.id.emoji);
        final ProgressBar pbar=(ProgressBar)holder.view.findViewById(R.id.pBar);
        TextView postTime=(TextView)holder.view.findViewById(R.id.postTime);
        // need this to fetch the drawable
         img=data.get(position).getEmoji();
         res= context.getResources();
        int resID = res.getIdentifier("emoji_"+img, "drawable", context.getPackageName());
        emo.setImageResource(resID);

        ImageLoader.getInstance().displayImage(data.get(position).getImg_url(), image, new ImageLoadingListener() {
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
        }); // to load image using library

        caption.setText(data.get(position).getCaption());
        postTime.setText(data.get(position).hoursAgo());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }
}