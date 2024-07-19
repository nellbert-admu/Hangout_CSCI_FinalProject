package com.example.hangout_csci_finalproject;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Adapter for displaying places in a RecyclerView.
 * This adapter is used to bind the data from a Realm database to views that are displayed within a RecyclerView.
 * It handles the creation of view holders and binds the place data to the views.
 */

public class PlaceAdapter extends RealmRecyclerViewAdapter<Place, PlaceAdapter.ViewHolder> {
    Home activity; // Reference to the MainActivity to access its methods.

    /**
     * Constructor for PlaceAdapter.
     * Initializes the adapter with the activity context, data, and autoUpdate flag.
     *
     * @param activity   The activity context.
     * @param data       The data to be displayed, fetched from Realm database.
     * @param autoUpdate If true, updates to the data will automatically trigger updates to the RecyclerView.
     */
    public PlaceAdapter(Home activity, @Nullable OrderedRealmCollection<Place> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = activity;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder's itemView to reflect the item at the given position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place u = getItem(position);

        holder.name.setText(u.getName());
        holder.location.setText(u.getLocation());

        // Set click listeners for delete and edit buttons
        holder.deleteButton.setOnClickListener(view -> activity.deletePlace(u));
        holder.editButton.setOnClickListener(view -> activity.editPlace(u));

        // Set click listener for the place image, opening the PlaceDetail activity
        holder.placeImg.setOnClickListener(view -> {
            Intent intent = new Intent(activity, PlaceDetail.class);
            intent.putExtra("place_id", u.getId());
            activity.startActivity(intent);
        });

        // Code for loading images with Picasso is commented out
    }

    /**
     * ViewHolder class for the RecyclerView items.
     * Holds the views that will display a place's information.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeImg; // ImageView for displaying the place's image.
        TextView name; // TextView for displaying the place's name.
        TextView location; // TextView for displaying the place's location.
        ImageButton deleteButton; // Button for deleting the place entry.
        ImageButton editButton; // Button for editing the place entry.

        /**
         * Constructor for ViewHolder.
         * Initializes the views from the itemView.
         *
         * @param itemView The view of the RecyclerView item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.place_name);
            location = itemView.findViewById(R.id.place_loc);
            placeImg = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}