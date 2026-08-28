package com.eaut.footballclubmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.eaut.footballclubmanagement.models.Player;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> playerList;
    private OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onEdit(Player player, View cardRoot);
        void onDelete(Player player);
    }

    public PlayerAdapter(List<Player> playerList, OnPlayerClickListener listener) {
        this.playerList = playerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_card, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.tvOvr.setText(String.valueOf(player.getOvr()));
        holder.tvPosition.setText(player.getPosition());
        holder.tvName.setText(player.getFullName());
        holder.tvGoals.setText("⚽ " + player.getGoals() + " Bàn");
        holder.tvMvp.setText("⭐ " + player.getMvp() + " MVP");
        holder.tvJersey.setText("#" + player.getJerseyNumber());

        // Bấm vào thẻ để Xem Chi tiết kèm theo View root để làm Animation
        holder.itemView.setOnClickListener(v -> listener.onEdit(player, holder.itemView));
        
        // Nhấn giữ (Long click) để xóa
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(player);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return playerList != null ? playerList.size() : 0;
    }

    public void updateData(List<Player> newPlayers) {
        this.playerList = newPlayers;
        notifyDataSetChanged();
    }

    public Player getPlayerAt(int position) {
        return playerList.get(position);
    }

    public void removePlayerAt(int position) {
        playerList.remove(position);
        notifyItemRemoved(position);
    }

    public void restorePlayer(Player player, int position) {
        playerList.add(position, player);
        notifyItemInserted(position);
    }

    public void filterList(List<Player> filteredList) {
        this.playerList = filteredList;
        notifyDataSetChanged();
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
