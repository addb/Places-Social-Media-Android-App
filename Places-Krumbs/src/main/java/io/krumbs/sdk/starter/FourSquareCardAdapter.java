package io.krumbs.sdk.starter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ADDB Inc on 14-03-2016.
 */
public class FourSquareCardAdapter extends RecyclerView.Adapter<FourSquareCardAdapter.ViewHolder>{

    private ArrayList<FourSquareActivity.FourSquareExplore> data;
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
    public FourSquareCardAdapter(ArrayList<FourSquareActivity.FourSquareExplore> data) {
        this.data = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FourSquareCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_business_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView name= (TextView) holder.view.findViewById(R.id.bname);
        TextView url= (TextView) holder.view.findViewById(R.id.burl);
        TextView phone= (TextView) holder.view.findViewById(R.id.bphone);
        TextView timings= (TextView) holder.view.findViewById(R.id.btime);
        RatingBar rating= (RatingBar) holder.view.findViewById(R.id.brating);

        //setting all the views
        name.setText(data.get(position).getName());
        url.setText(data.get(position).getUrl());
        phone.setText(data.get(position).getPhone());
        timings.setText(data.get(position).getTime_status());
        rating.setRating((Float.parseFloat(data.get(position).getRating())/2));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }
}