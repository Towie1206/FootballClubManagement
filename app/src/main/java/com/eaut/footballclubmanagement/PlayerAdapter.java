package com.eaut.footballclubmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eaut.footballclubmanagement.models.Player;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PLAYER = 1;

    private List<Object> items;
    private OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onEdit(Player player, View cardRoot);
        void onDelete(Player player);
    }

    public PlayerAdapter(List<Object> items, OnPlayerClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_PLAYER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_card, parent, false);
            return new PlayerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            String title = (String) items.get(position);
            ((HeaderViewHolder) holder).tvHeaderTitle.setText(title);
        } else {
            Player player = (Player) items.get(position);
            PlayerViewHolder pvh = (PlayerViewHolder) holder;
            
            pvh.tvOvr.setText(String.valueOf(player.getOvr()));
            pvh.tvPosition.setText(player.getPosition());
            pvh.tvName.setText(player.getFullName());
            
            pvh.tvGoals.setText("⚽ " + player.getGoals() + " Goals");
            pvh.tvMvp.setText("⭐ " + player.getMvp() + " MVP");
            
            pvh.tvJersey.setText("#" + player.getJerseyNumber());

            pvh.itemView.setOnClickListener(v -> listener.onEdit(player, pvh.itemView));
            pvh.itemView.setOnLongClickListener(v -> {
                listener.onDelete(player);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateData(List<Object> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public Object getItemAt(int position) {
        return items.get(position);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeaderTitle;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderTitle = itemView.findViewById(R.id.tvHeaderTitle);
        }
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView tvOvr, tvPosition, tvName, tvGoals, tvMvp, tvJersey;
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOvr = itemView.findViewById(R.id.tvOvr);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvName = itemView.findViewById(R.id.tvName);
            tvGoals = itemView.findViewById(R.id.tvGoals);
            tvMvp = itemView.findViewById(R.id.tvMvp);
            tvJersey = itemView.findViewById(R.id.tvJersey);
        }
    }
}
