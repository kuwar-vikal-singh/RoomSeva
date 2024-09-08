package com.axel.roomseva.ui.Adapter;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.axel.roomseva.R;
import com.axel.roomseva.ui.Model.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Room room);
    }

    public RoomAdapter(List<Room> roomList, OnItemClickListener listener) {
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomNumberTextView.setText(room.getRoomNumber());
        holder.statusTextView.setText(room.getStatus());

        // Set status color based on room availability
        if ("Available".equals(room.getStatus())) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.hotelRoomKey.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.black), PorterDuff.Mode.SRC_IN);
        } else {
            holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            holder.hotelRoomKey.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.red), PorterDuff.Mode.SRC_IN);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(room));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView hotelRoomKey;
        TextView roomNumberTextView;
        TextView statusTextView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelRoomKey = itemView.findViewById(R.id.hotelRoomKey);
            roomNumberTextView = itemView.findViewById(R.id.roomNumberTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}
