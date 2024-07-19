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

public class PlaceAdapter extends RealmRecyclerViewAdapter<Place, PlaceAdapter.ViewHolder> {
    Home activity; // Reference to the MainActivity to access its methods.

    public PlaceAdapter(Home activity, @Nullable OrderedRealmCollection<Place> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place u = getItem(position);

        holder.name.setText(u.getName());
        holder.location.setText(u.getLocation());

        holder.deleteButton.setOnClickListener(view -> activity.deletePlace(u));
        holder.editButton.setOnClickListener(view -> activity.editPlace(u));

        holder.placeImg.setOnClickListener(view -> {
            Intent intent = new Intent(activity, PlaceDetail.class);
            intent.putExtra("place_id", u.getId());
            activity.startActivity(intent);
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeImg;
        TextView name;
        TextView location;
        ImageButton deleteButton;
        ImageButton editButton;

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
